package com.happyplaces.data.repository

import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HappyPlaceRepositoryImpl(private val dao: UserDao) : HappyPlaceRepository {
    override val dataList = dao.getAllData()
    override suspend fun insert(happyPlaceEntity: HappyPlaceEntity): Long = dao.insertData(happyPlaceEntity)
    override suspend fun update(happyPlaceEntity: HappyPlaceEntity): Int = dao.updateData(happyPlaceEntity)
    override suspend fun delete(happyPlaceEntity: HappyPlaceEntity): Int = dao.deleteData(happyPlaceEntity)
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlaceEntity>> = flow {
        emit(ApiResource.Loading())
        val happyPlace = dao.getHappyPlaceById(id)
        happyPlace?.let {
            emit(ApiResource.Success(it))
        } ?: emit(ApiResource.Error("查询失败"))
    }.flowOn(Dispatchers.IO)
}