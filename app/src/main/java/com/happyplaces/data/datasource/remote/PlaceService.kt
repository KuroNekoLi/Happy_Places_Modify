package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

interface PlaceService {
    fun getPlaces(): Flow<List<PlaceDto>>

    /** 新增一筆文章，回傳 documentId */
    suspend fun addPlace(place: PlaceDto): String
    suspend fun updatePlace(place: PlaceDto)

    /** 刪除指定 documentId 的文章 */
    suspend fun deletePlace(id: String)
    fun getPlaceByIdFlow(id: String): Flow<PlaceDto>

    fun getUsers(): Flow<List<UserDto>>
    suspend fun addUser(user: UserDto): String
    suspend fun updateUser(user: UserDto)
    suspend fun deleteUser(id: String)
    fun getUserByIdFlow(id: String): Flow<UserDto>
    suspend fun isUserProfileCompleted(id: String): Boolean
    suspend fun getCurrentUser(): UserDto?
}