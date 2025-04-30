package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

class PlaceRemoteDataSource(private val placeService: PlaceService) {
    suspend fun getPlaces(): Flow<List<PlaceDao>> = placeService.getPlaces()
}