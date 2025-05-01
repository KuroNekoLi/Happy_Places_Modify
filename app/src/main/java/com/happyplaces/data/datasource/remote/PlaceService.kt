package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

interface PlaceService {
    val generatedId: String
    fun getPlaces():Flow<List<PlaceDao>>
}