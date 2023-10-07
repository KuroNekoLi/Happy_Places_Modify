package com.happyplaces.models

import android.net.Uri

data class HappyPlaceModel(
    val id:Int,
    val title:String,
    val image: Uri?,
    val description: String,
    val date:String,
    val location:String,
    val latitude:Double,
    val longitude:Double
)
