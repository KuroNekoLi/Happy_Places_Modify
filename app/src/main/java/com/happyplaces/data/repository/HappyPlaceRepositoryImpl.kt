package com.happyplaces.data.repository

import androidx.core.net.toUri
import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.remote.PlaceDto
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.data.model.HappyPlace
import com.happyplaces.util.ApiResource
import com.happyplaces.util.toDateString
import com.happyplaces.util.toHappyPlace
import com.happyplaces.util.toHappyPlaceEntity
import com.happyplaces.util.toPlaceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform

class HappyPlaceRepositoryImpl(
    private val placeRemoteDataSource: PlaceRemoteDataSource,
    private val dao: UserDao
) : HappyPlaceRepository {
    override fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>> = flow {
        emit(ApiResource.Loading())

        // 1. 在遠端產生 DocumentReference 並上傳
        val id = placeRemoteDataSource.addPlace(happyPlace.toPlaceDto())

        // 2. 同步寫入本地
        val entity = happyPlace.copy(id = id).toHappyPlaceEntity()
        val row = dao.insertData(entity)

        emit(ApiResource.Success(row))
    }
        .onStart { /* optional */ }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllHappyPlaces(): Flow<ApiResource<List<HappyPlace>>> =
        placeRemoteDataSource.getPlaces()                    // 1. 遠端流: Flow<List<Dto>>
            .flatMapLatest { dtos ->
                // 把遠端 DTO 寫進 Room
                dao.insertAllData(dtos.map { it.toHappyPlaceEntity() })
                // 轉為本地 Entity Flow< List<HappyPlaceEntity> >
                dao.getAllData()
            }
            .transform { list ->
                // 先發 Loading
                emit(ApiResource.Loading<List<HappyPlace>>())
                // 再發 Success
                emit(ApiResource.Success(list.map { it.toHappyPlace() }))
            }
            .catch { e ->
                emit(ApiResource.Error<List<HappyPlace>>(e.message ?: "未知錯誤"))
            }
            .flowOn(Dispatchers.IO)
}

private fun PlaceDto.toHappyPlaceEntity(): HappyPlaceEntity {
    return HappyPlaceEntity(
        id          = this.id,
        title       = this.title,
        image       = this.imageUrl.toUri(),
        description = this.description,
        date        = this.createdAt.toDateString(),
        location    = this.address,
        latitude    = this.latitude,
        longitude   = this.longitude
    )
}
