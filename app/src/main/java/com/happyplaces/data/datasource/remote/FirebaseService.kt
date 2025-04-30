package com.happyplaces.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlaceService {
    override fun getPlaces(): Flow<List<PlaceDao>> =
        firestore.collection("articles")
            .snapshots()
            .map {
                it.toObjects(PlaceDao::class.java)
            }
}