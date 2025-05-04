package com.happyplaces.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_data_table")
data class HappyPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String?,
    val image: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
)
