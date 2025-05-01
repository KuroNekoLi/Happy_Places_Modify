package com.happyplaces.data.repository

import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.data.model.HappyPlace
import com.happyplaces.util.ApiResource
import com.happyplaces.util.toHappyPlace
import com.happyplaces.util.toHappyPlaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HappyPlaceRepositoryImpl(
    private val placeRemoteDataSource: PlaceRemoteDataSource,
    private val dao: UserDao
) : HappyPlaceRepository {
    override val dataList = dao.getAllData().map { happyPlaceEntityList ->
        happyPlaceEntityList.map {
            it.toHappyPlace()
        }
    }
    override suspend fun insert(happyPlace: HappyPlace): Long = dao.insertData(happyPlace.toHappyPlaceEntity())
    override suspend fun update(happyPlace: HappyPlace): Int = dao.updateData(happyPlace.toHappyPlaceEntity())
    override suspend fun delete(happyPlace: HappyPlace): Int = dao.deleteData(happyPlace.toHappyPlaceEntity())
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlace>> = flow {
        emit(ApiResource.Loading())
        val happyPlace = dao.getHappyPlaceById(id)?.toHappyPlace()
        happyPlace?.let {
            emit(ApiResource.Success(it))
        } ?: emit(ApiResource.Error("查询失败"))
    }.flowOn(Dispatchers.IO)
}