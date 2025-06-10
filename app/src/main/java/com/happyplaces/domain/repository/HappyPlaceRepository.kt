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

    /**
     * 取得其他人的快樂地點（不包含自己的）
     */
    fun getOthersHappyPlaces(): Flow<ApiResource<List<HappyPlace>>>

    /**
     * 搜尋快樂地點
     * @param query 搜尋關鍵字
     * @param includeMyPlaces 是否包含自己的地點
     */
    fun searchHappyPlaces(
        query: String,
        includeMyPlaces: Boolean = true
    ): Flow<ApiResource<List<HappyPlace>>>
    
    fun getMyPlaces(): Flow<ApiResource<List<HappyPlace>>>
    fun updateAllHappyPlaces(): Flow<ApiResource<Unit>>

    /**
     * 獲取分頁的快樂地點資料
     * 從 Firebase 獲取資料並過濾掉自己的資料
     */
    suspend fun getAllHappyPlacesPaged(
        pageSize: Int,
        lastDocument: com.google.firebase.firestore.DocumentSnapshot?
    ): ApiResource<Pair<List<HappyPlace>, com.google.firebase.firestore.DocumentSnapshot?>>
}
