package com.happyplaces.data.repository

import com.happyplaces.data.model.HappyPlace
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockHappyPlaceRepository: HappyPlaceRepository {
    override val dataList:Flow<List<HappyPlace>> = flow {
        emit(mockHappyPlaceLists)
    }
    override suspend fun insert(happyPlace: HappyPlace): Long = mockHappyPlaceLists.size.toLong()
    override suspend fun update(happyPlace: HappyPlace): Int = mockHappyPlaceLists.indexOf(happyPlace)
    override suspend fun delete(happyPlace: HappyPlace): Int = mockHappyPlaceLists.indexOf(happyPlace)
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlace>> = flow {
        emit(ApiResource.Success(mockHappyPlaceLists.first { it.id == id }))
    }
}