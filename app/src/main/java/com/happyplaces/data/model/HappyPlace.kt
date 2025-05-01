package com.happyplaces.data.model

import android.net.Uri

data class HappyPlace(
    val id: String = "",
    val title: String?,
    val image: Uri?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
)
