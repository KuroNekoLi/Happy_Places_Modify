package com.happyplaces.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await

/** 文章資料庫 */
const val ARTICLE_COLLECTION = "articles"

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlaceService {
    override fun getPlaces(): Flow<List<PlaceDto>> =
        firestore.collection(ARTICLE_COLLECTION)
            .snapshots()
            .map {
                it.toObjects(PlaceDto::class.java)
            }
            .catch { e -> emit(emptyList()) }

    override suspend fun addPlace(place: PlaceDto): String {
        // 1. 取得一個新的 DocumentReference
        val docRef = firestore.collection(ARTICLE_COLLECTION).document()
        // 2. 如果你的 PlaceDto 裡也有 id 欄位，先把剛拿到的 ID 填回去
        val withId = place.copy(id = docRef.id)
        // 3. 寫入 Firestore
        docRef.set(withId).await()   // 需要 kotlinx-coroutines-play-services
        // 4. 回傳這筆資料的 ID
        return docRef.id
    }

    override suspend fun updatePlace(place: PlaceDto) {
        // 假設 PlaceDto.id 就是 documentId
        firestore.collection(ARTICLE_COLLECTION)
            .document(place.id)
            .set(place, SetOptions.merge())   // 或用 .update(map) 做部分欄位更新
            .await()
    }

    override suspend fun deletePlace(id: String) {
        firestore.collection(ARTICLE_COLLECTION)
            .document(id)
            .delete()
            .await()  // 需引入 kotlinx-coroutines-play-services 才能 await()
    }

    override fun getPlaceByIdFlow(id: String): Flow<PlaceDto> =
        firestore.collection(ARTICLE_COLLECTION)
            .document(id)
            .snapshots()
            .mapNotNull { it.toObject(PlaceDto::class.java)?.copy(id = it.id) }
}