package com.happyplaces.util

import androidx.core.net.toUri
import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.datasource.remote.PlaceDao
import com.happyplaces.data.model.HappyPlace
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun HappyPlaceEntity.toHappyPlace(): HappyPlace = HappyPlace(
    id = id,
    title = title,
    image = image,
    description = description,
    date = date,
    location = location,
    latitude = latitude,
    longitude = longitude
)

fun PlaceDao.toHappyPlace(): HappyPlace {
    val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
    val formattedDate = dateFormat.format(Date(this.visitDate))

    return HappyPlace(
        id = 0, // 因為 Room 會 autoGenerate，所以設為 0
        title = this.title,
        image = this.imageUrl.toUri(),
        description = this.description,
        date = formattedDate,
        location = this.address,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun List<PlaceDao>.toHappyPlace(): List<HappyPlace> {
    return this.map { it.toHappyPlace() }
}

fun HappyPlace.toHappyPlaceEntity() = HappyPlaceEntity(
    id = this.id,
    title = this.title,
    image = this.image,
    description = this.description,
    date = this.date,
    location = this.location,
    latitude = this.latitude,
    longitude = this.longitude
)