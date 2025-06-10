package com.happyplaces.data.datasource.remote

import kotlinx.coroutines.flow.Flow

interface PlaceService {
    fun getPlaces(): Flow<List<PlaceDto>>

    /**
     * 取得其他人的地點（不包含自己的）
     */
    fun getOthersPlaces(): Flow<List<PlaceDto>>

    /**
     * 搜尋地點
     * @param query 搜尋關鍵字
     * @param includeMyPlaces 是否包含自己的地點
     */
    fun searchPlaces(query: String, includeMyPlaces: Boolean = true): Flow<List<PlaceDto>>

    /**
     * 分頁取得其他人的地點
     * @param pageSize 每頁數量
     * @param lastDocument 上一頁的最後一個文件
     */
    suspend fun getOthersPlacesPaged(
        pageSize: Int,
        lastDocument: com.google.firebase.firestore.DocumentSnapshot? = null
    ): Pair<List<PlaceDto>, com.google.firebase.firestore.DocumentSnapshot?>

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
    fun getMyPlaces(): Flow<List<PlaceDto>>
}
