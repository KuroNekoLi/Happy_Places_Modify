package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

class PlaceRemoteDataSource(private val placeService: PlaceService) {
    fun getPlaces(): Flow<List<PlaceDto>> = placeService.getPlaces()

    /**
     * 取得其他人的地點（不包含自己的）
     */
    fun getOthersPlaces(): Flow<List<PlaceDto>> = placeService.getOthersPlaces()

    /**
     * 搜尋地點
     */
    fun searchPlaces(query: String, includeMyPlaces: Boolean = true): Flow<List<PlaceDto>> =
        placeService.searchPlaces(query, includeMyPlaces)

    /**
     * 分頁取得其他人的地點
     */
    suspend fun getOthersPlacesPaged(
        pageSize: Int,
        lastDocument: com.google.firebase.firestore.DocumentSnapshot? = null
    ): Pair<List<PlaceDto>, com.google.firebase.firestore.DocumentSnapshot?> =
        placeService.getOthersPlacesPaged(pageSize, lastDocument)
    
    suspend fun addPlace(place: PlaceDto): String = placeService.addPlace(place)
    suspend fun updatePlace(place: PlaceDto) = placeService.updatePlace(place)
    suspend fun deletePlace(id: String) = placeService.deletePlace(id)
    fun getPlaceByIdFlow(id: String): Flow<PlaceDto> = placeService.getPlaceByIdFlow(id)
}
