package com.happyplaces.util

import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.remote.PlaceDao
import java.text.SimpleDateFormat
import java.util.Locale

const val dateFormatPattern = "yyyy.MM.dd"

fun happyPlaceEntityToPlaceDao(entity: HappyPlaceEntity, creatorId: String): PlaceDao {
    val parsedDate = entity.date?.let {
        try {
            SimpleDateFormat(dateFormatPattern, Locale.getDefault()).parse(it)?.time ?: 0L
        } catch (e: Exception) {
            0L // fallback if date parsing fails
        }
    } ?: 0L

    return PlaceDao(
        id = "", // Firestore 會生成，這裡留空或你可以自定義
        creatorId = creatorId,
        title = entity.title.orEmpty(),
        description = entity.description.orEmpty(),
        visitDate = parsedDate,
        address = entity.location.orEmpty(),
        latitude = entity.latitude,
        longitude = entity.longitude,
        imageUrl = entity.image?.toString().orEmpty(),
        createdAt = System.currentTimeMillis() // 或用你的邏輯決定
    )
}