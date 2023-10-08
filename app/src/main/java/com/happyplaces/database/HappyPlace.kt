package com.happyplaces.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_data_table")
data class HappyPlace(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val title:String,
    val image: Uri?,
    val description: String,
    val date:String,
    val location:String,
    val latitude:Double,
    val longitude:Double
)
