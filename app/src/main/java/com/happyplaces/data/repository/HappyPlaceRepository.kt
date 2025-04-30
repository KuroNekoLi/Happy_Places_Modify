package com.happyplaces.data.repository

import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface HappyPlaceRepository{
    val dataList: Flow<List<HappyPlaceEntity>>
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity): Long
    suspend fun update(happyPlaceEntity: HappyPlaceEntity): Int
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity): Int
    suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlaceEntity>>
}