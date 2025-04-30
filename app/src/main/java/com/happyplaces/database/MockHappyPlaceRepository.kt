package com.happyplaces.database

import com.happyplaces.mockHappyPlaceList
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockHappyPlaceRepository: HappyPlaceRepository {
    override val dataList:Flow<List<HappyPlace>> = flow {
        emit(mockHappyPlaceList)
    }
    override suspend fun insert(happyPlace: HappyPlace): Long = mockHappyPlaceList.size.toLong()
    override suspend fun update(happyPlace: HappyPlace): Int = mockHappyPlaceList.indexOf(happyPlace)
    override suspend fun delete(happyPlace: HappyPlace): Int = mockHappyPlaceList.indexOf(happyPlace)
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlace>> = flow {
        emit(ApiResource.Success(mockHappyPlaceList.first { it.id == id }))
    }
}