package com.happyplaces.data.repository

import android.util.Log
import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.remote.PlaceDto
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.domain.HappyPlaceRepository
import com.happyplaces.domain.model.HappyPlace
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault

class HappyPlaceRepositoryImpl(
    private val placeRemoteDataSource: PlaceRemoteDataSource,
    private val dao: UserDao
) : HappyPlaceRepository {
    override fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>> = flow {
        emit(ApiResource.Loading())  // 1. 本地插入前先發 Loading
        // 2. 本地寫入並取得自動遞增 rowId
        val entity = happyPlace.toHappyPlaceEntity()
        val rowId = dao.insertData(entity)  // Room 回傳自動產生的主鍵
        emit(ApiResource.Success(rowId))     // 3. 立即回傳 Success 結果
        // 4. Fire-and-forget 背景同步至遠端，不阻塞上方流程
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val dtoWithId = happyPlace.copy(id = rowId.toString()).toPlaceDto()
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
        // 遠端更新
        placeRemoteDataSource.updatePlace(place = happyPlace.toPlaceDto())

        // 本地更新
        val count = dao.updateData(happyPlace.toHappyPlaceEntity())
        emit(ApiResource.Success(count))
    }
        .catch { e -> emit(ApiResource.Error(e.localizedMessage ?: "更新失敗")) }
        .flowOn(Dispatchers.IO)

    override fun delete(happyPlace: HappyPlace): Flow<ApiResource<Int>> = flow {
        emit(ApiResource.Loading())
        // 遠端刪除
        placeRemoteDataSource.deletePlace(id = happyPlace.id)

        // 本地刪除
        val count = dao.deleteData(happyPlace.toHappyPlaceEntity())
        emit(ApiResource.Success(count))
    }
        .catch { e -> emit(ApiResource.Error(e.localizedMessage ?: "刪除失敗")) }
        .flowOn(Dispatchers.IO)    // Repository 層：不需要加 suspend，因為 Flow 本身就是 cold stream

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHappyPlaceById(id: String): Flow<ApiResource<HappyPlace>> =
        placeRemoteDataSource.getPlaceByIdFlow(id)        // Flow<PlaceDto>
            .flatMapLatest { dto ->
                // 1. 寫入 Room
                val entity = dto.toHappyPlaceEntity()
                dao.insertData(entity)
                // 2. 回傳本地 Flow
                dao.getHappyPlaceByIdFlow(id)             // Flow<HappyPlaceEntity?>
            }
            .map { entity ->
                entity?.toHappyPlace()
                    ?.let { ApiResource.Success(it) }
                    ?: ApiResource.Error("查無 id = $id 的資料")
            }
            .onStart { emit(ApiResource.Loading()) }
            .catch { e -> emit(ApiResource.Error(e.message ?: "未知錯誤")) }
            .flowOn(Dispatchers.IO)

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


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun updateAllHappyPlaces(): Flow<ApiResource<Unit>> {
        Log.i("LinLi", "updateAllHappyPlaces() called")
        return placeRemoteDataSource.getPlaces()                    // 1. 遠端流: Flow<List<Dto>>
            .flatMapLatest { dtos ->
                Log.i("LinLi", "updateAllHappyPlaces dtos: $dtos")
                // 把遠端 DTO 寫進 Room
                dao.clearAllData()
                dao.insertAllData(dtos.map { it.toHappyPlaceEntity() }).apply {
                    Log.i("LinLi", "updateAllHappyPlaces: 新增 ${this} 筆資料")
                }
                // 轉為本地 Entity Flow< List<HappyPlaceEntity> >
                dao.getAllData()
            }
            .transform { list ->
                // 先發 Loading
                emit(ApiResource.Loading())
                // 再發 Success
                emit(ApiResource.Success(Unit))
            }
            .catch { e ->
                Log.i("LinLi", "updateAllHappyPlaces: 更新失敗 $e")
                emit(ApiResource.Error(e.message ?: "未知錯誤"))
            }
            .flowOn(Dispatchers.IO)
    }
}

private fun PlaceDto.toHappyPlaceEntity(): HappyPlaceEntity {
    return HappyPlaceEntity(
        id = this.id.toLongOrDefault(0),
        title = this.title,
        image = this.imageUrl,
        description = this.description,
        date = this.createdAt.toDateString(),
        location = this.address,
        latitude = this.latitude,
        longitude = this.longitude
    )
}
