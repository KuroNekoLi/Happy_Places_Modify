package com.happyplaces.data.repository

import android.util.Log
import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.remote.PlaceDto
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.data.datasource.remote.PlaceService
import com.happyplaces.domain.AuthProvider
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.domain.repository.HappyPlaceRepository
import com.happyplaces.util.ApiResource
import com.happyplaces.util.toDateString
import com.happyplaces.util.toHappyPlace
import com.happyplaces.util.toHappyPlaceEntity
import com.happyplaces.util.toPlaceDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class HappyPlaceRepositoryImpl(
    private val placeRemoteDataSource: PlaceRemoteDataSource,
    private val placeService: PlaceService,
    private val dao: UserDao,
    private val authProvider: AuthProvider
) : HappyPlaceRepository {
    override fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>> = flow {
        emit(ApiResource.Loading())  // 1. 本地插入前先發 Loading
        val id = UUID.randomUUID().toString()
        val currentUser = placeService.getCurrentUser()
        val finalHappyPlace = happyPlace.copy(
            creatorId = currentUser?.id.orEmpty(),
            id = id
        )
        val entity = finalHappyPlace.toHappyPlaceEntity()
        val rowId = dao.insertData(entity)  // Room 回傳自動產生的主鍵
        emit(ApiResource.Success(rowId))     // 3. 立即回傳 Success 結果
        // 4. Fire-and-forget 背景同步至遠端，不阻塞上方流程
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val dtoWithId = finalHappyPlace.toPlaceDto()
                placeRemoteDataSource.addPlace(dtoWithId)
            } catch (e: Exception) {
                // 可選：記錄錯誤或 retry
                Log.e("Repo", "Remote sync failed", e)
            }
        }
    }
        .catch { e -> emit(ApiResource.Error(e.localizedMessage ?: "新增失敗")) }
        .flowOn(Dispatchers.IO)

    override fun update(happyPlace: HappyPlace): Flow<ApiResource<Int>> = flow {
        emit(ApiResource.Loading())
        // 取得當前使用者
        val currentUser = authProvider.getCurrentUser().firstOrNull()
        // 建立含使用者 ID 的更新資料
        val newPlace = happyPlace.copy(creatorId = currentUser?.id.orEmpty())
        // 本地更新
        val count = dao.updateData(newPlace.toHappyPlaceEntity())
        emit(ApiResource.Success(count))
        // Fire-and-forget 背景遠端更新
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                placeRemoteDataSource.updatePlace(newPlace.toPlaceDto())
            } catch (e: Exception) {
                Log.e("Repo", "Remote update failed", e)
            }
        }
    }
        .catch { e -> emit(ApiResource.Error(e.localizedMessage ?: "更新失敗")) }
        .flowOn(Dispatchers.IO)

    override fun delete(happyPlace: HappyPlace): Flow<ApiResource<Int>> = flow {
        emit(ApiResource.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            // 遠端刪除
            placeRemoteDataSource.deletePlace(id = happyPlace.id)
        }

        // 本地刪除
        val count = dao.deleteData(happyPlace.toHappyPlaceEntity())
        emit(ApiResource.Success(count))
    }
        .catch { e -> emit(ApiResource.Error(e.localizedMessage ?: "刪除失敗")) }
        .flowOn(Dispatchers.IO)    // Repository 層：不需要加 suspend，因為 Flow 本身就是 cold stream

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHappyPlaceById(id: String): Flow<ApiResource<HappyPlace>> =
        dao.getHappyPlaceByIdFlow(id).map { entity ->
            entity?.toHappyPlace()
                ?.let { ApiResource.Success(it) }
                ?: ApiResource.Error("查無 id = $id 的資料")
        }

    override fun getAllHappyPlaces(): Flow<ApiResource<List<HappyPlace>>> = flow {
        // 1. 發送 Loading 狀態
        emit(ApiResource.Loading())
        // 2. 收集本地資料庫的所有資料並發射 Success
        dao.getAllData().collect { entities ->
            val list = entities.map { it.toHappyPlace() }
            emit(ApiResource.Success(list))
        }
    }
        .catch { e -> emit(ApiResource.Error(e.message ?: "未知錯誤")) }
        .flowOn(Dispatchers.IO)

    override fun getMyPlaces(): Flow<ApiResource<List<HappyPlace>>> =
        flow {
            // 1. 發送 Loading 狀態
            emit(ApiResource.Loading())
            // 2. 收集本地資料庫的所有資料並發射 Success
            authProvider.getMyId()?.let {
                dao.getPlacesByCreatorId(it).collect { entities ->
                    val list = entities.map { it.toHappyPlace() }
                    emit(ApiResource.Success(list))
                }
            } ?: emit(ApiResource.Error("未登入"))
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun updateAllHappyPlaces(): Flow<ApiResource<Unit>> = flow {
        emit(ApiResource.Loading())
        // 1. 取得遠端資料
        val remoteDtos = placeRemoteDataSource.getPlaces().drop(1).first()
        Log.i("LinLi", "remoteDtos : $remoteDtos")
        // 2. 取得本地現有資料
        val localEntities = dao.getAllDataList()
        // 3. 計算要刪除的 local id、以及本地 id Set
        val remoteIds = remoteDtos.map { it.id }.toSet()
        val localIds = localEntities.map { it.id }.toSet()
        val toDelete = localEntities.filter { it.id !in remoteIds }
        // 4. 刪除不存在於遠端的本地項目
        toDelete.forEach { dao.deleteData(it) }
        // 5. 同步新增或更新
        remoteDtos.forEach { dto ->
            val entity = dto.toHappyPlaceEntity()
            if (entity.id in localIds) {
                dao.updateData(entity)
            } else {
                dao.insertData(entity)
            }
        }
        emit(ApiResource.Success(Unit))
    }
        .catch { e ->
            Log.i("LinLi", "updateAllHappyPlaces: 更新失敗 $e")
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
        .flowOn(Dispatchers.IO)
}

private fun PlaceDto.toHappyPlaceEntity(): HappyPlaceEntity {
    return HappyPlaceEntity(
        id = this.id,
        title = this.title,
        image = this.imageUrl,
        description = this.description,
        date = this.createdAt.toDateString(),
        location = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        creatorId = this.creatorId
    )
}
