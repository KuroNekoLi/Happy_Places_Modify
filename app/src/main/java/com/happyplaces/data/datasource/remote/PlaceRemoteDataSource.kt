package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

class PlaceRemoteDataSource(private val placeService: PlaceService) {
    fun getPlaces(): Flow<List<PlaceDto>> = placeService.getPlaces()
    suspend fun addPlace(place: PlaceDto): String = placeService.addPlace(place)
    suspend fun updatePlace(place: PlaceDto) = placeService.updatePlace(place)
    suspend fun deletePlace(id: String) = placeService.deletePlace(id)
    fun getPlaceByIdFlow(id: String): Flow<PlaceDto> = placeService.getPlaceByIdFlow(id)
}