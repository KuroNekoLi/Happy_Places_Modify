package com.happyplaces.database

import com.happyplaces.util.ApiResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HappyPlaceRepositoryImpl(private val dao: UserDao) : HappyPlaceRepository {
    override val dataList = dao.getAllData()
    override suspend fun insert(happyPlace: HappyPlace): Long = dao.insertData(happyPlace)
    override suspend fun update(happyPlace: HappyPlace): Int = dao.updateData(happyPlace)
    override suspend fun delete(happyPlace: HappyPlace): Int = dao.deleteData(happyPlace)
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlace>> = flow {
        emit(ApiResource.Loading())
        val happyPlace = dao.getHappyPlaceById(id)
        happyPlace?.let {
            emit(ApiResource.Success(it))
        } ?: emit(ApiResource.Error("查询失败"))
    }.flowOn(Dispatchers.IO)
}