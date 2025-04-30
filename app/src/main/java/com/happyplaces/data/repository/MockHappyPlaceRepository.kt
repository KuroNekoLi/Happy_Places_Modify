package com.happyplaces.data.repository

import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.model.mockHappyPlaceEntityLists
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockHappyPlaceRepository: HappyPlaceRepository {
    override val dataList:Flow<List<HappyPlaceEntity>> = flow {
        emit(mockHappyPlaceEntityLists)
    }
    override suspend fun insert(happyPlaceEntity: HappyPlaceEntity): Long = mockHappyPlaceEntityLists.size.toLong()
    override suspend fun update(happyPlaceEntity: HappyPlaceEntity): Int = mockHappyPlaceEntityLists.indexOf(happyPlaceEntity)
    override suspend fun delete(happyPlaceEntity: HappyPlaceEntity): Int = mockHappyPlaceEntityLists.indexOf(happyPlaceEntity)
    override suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlaceEntity>> = flow {
        emit(ApiResource.Success(mockHappyPlaceEntityLists.first { it.id == id }))
    }
}