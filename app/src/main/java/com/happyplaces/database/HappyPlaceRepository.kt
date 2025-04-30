package com.happyplaces.database

import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface HappyPlaceRepository{
    val dataList: Flow<List<HappyPlace>>
    suspend fun insert(happyPlace: HappyPlace): Long
    suspend fun update(happyPlace: HappyPlace): Int
    suspend fun delete(happyPlace: HappyPlace): Int
    suspend fun getHappyPlaceById(id: Int): Flow<ApiResource<HappyPlace>>
}