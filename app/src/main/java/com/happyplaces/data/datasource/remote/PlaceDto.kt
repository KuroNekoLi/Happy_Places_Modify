package com.happyplaces.data.datasource.remote

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PlaceDto(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val description: String = "",
    val visitDate: Long = 0L,
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val createdAt: Long = 0L
)