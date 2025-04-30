package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

interface PlaceService {
    fun getPlaces():Flow<List<PlaceDao>>
}