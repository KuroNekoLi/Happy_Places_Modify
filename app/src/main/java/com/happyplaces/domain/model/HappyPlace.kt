package com.happyplaces.domain.model

data class HappyPlace(
    val id: String = "",
    val title: String?,
    val image: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
)