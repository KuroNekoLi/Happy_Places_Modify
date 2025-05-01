package com.happyplaces.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** 文章資料庫 */
const val ARTICLE_COLLECTION = "articles"
class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlaceService {
    override val generatedId = firestore.collection(ARTICLE_COLLECTION).document().id
    override fun getPlaces(): Flow<List<PlaceDao>> =
        firestore.collection(ARTICLE_COLLECTION)
            .snapshots()
            .map {
                it.toObjects(PlaceDao::class.java)
            }
}