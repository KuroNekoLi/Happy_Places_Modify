package com.happyplaces.domain.repository

import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface HappyPlaceRepository {
    fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>>
    fun update(happyPlace: HappyPlace): Flow<ApiResource<Int>>
    fun delete(happyPlace: HappyPlace): Flow<ApiResource<Int>>
    fun getHappyPlaceById(id: String): Flow<ApiResource<HappyPlace>>
    fun getAllHappyPlaces(): Flow<ApiResource<List<HappyPlace>>>
    fun getMyPlaces(): Flow<ApiResource<List<HappyPlace>>>
    fun updateAllHappyPlaces(): Flow<ApiResource<Unit>>
}