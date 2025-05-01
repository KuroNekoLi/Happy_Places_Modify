package com.happyplaces.data.repository

import com.happyplaces.data.model.HappyPlace
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface HappyPlaceRepository{
    fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>>
     fun update(happyPlace: HappyPlace): Flow<ApiResource<Int>>
     fun delete(happyPlace: HappyPlace): Flow<ApiResource<Int>>
     fun getHappyPlaceById(id: String): Flow<ApiResource<HappyPlace>>
    fun getAllHappyPlaces(): Flow<ApiResource<List<HappyPlace>>>
}