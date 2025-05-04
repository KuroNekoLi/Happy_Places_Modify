package com.happyplaces.util

import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.remote.PlaceDto
import com.happyplaces.domain.model.HappyPlace
import okhttp3.internal.toLongOrDefault
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun HappyPlaceEntity.toHappyPlace(): HappyPlace = HappyPlace(
    id = id.toString(),
    title = title,
    image = image,
    description = description,
    date = date,
    location = location,
    latitude = latitude,
    longitude = longitude
)

fun PlaceDto.toHappyPlace(): HappyPlace {
    val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
    val formattedDate = dateFormat.format(Date(this.visitDate))

    return HappyPlace(
        id = "0", // 因為 Room 會 autoGenerate，所以設為 0
        title = this.title,
        image = this.imageUrl,
        description = this.description,
        date = formattedDate,
        location = this.address,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun List<PlaceDto>.toHappyPlace(): List<HappyPlace> {
    return this.map { it.toHappyPlace() }
}

fun HappyPlace.toHappyPlaceEntity() = HappyPlaceEntity(
    id = this.id.toLongOrDefault(0),
    title = this.title,
    image = this.image,
    description = this.description,
    date = this.date,
    location = this.location,
    latitude = this.latitude,
    longitude = this.longitude
)

const val dateFormatPattern = "yyyy.MM.dd"

fun HappyPlace.toPlaceDto(): PlaceDto {
    val parsedDate = this.date?.let {
        try {
            SimpleDateFormat(dateFormatPattern, Locale.getDefault()).parse(it)?.time ?: 0L
        } catch (e: Exception) {
            0L // fallback if date parsing fails
        }
    } ?: 0L

    return PlaceDto(
        id = "",
        creatorId = "Lin",
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        visitDate = parsedDate,
        address = this.location.orEmpty(),
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.image?.toString().orEmpty(),
        createdAt = System.currentTimeMillis()
    )
}