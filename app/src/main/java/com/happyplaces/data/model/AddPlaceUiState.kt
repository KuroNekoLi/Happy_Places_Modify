package com.happyplaces.data.model

import android.net.Uri
import com.happyplaces.data.datasource.local.HappyPlaceEntity

data class AddPlaceUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUri: Uri? = null,
    val event: AddPlaceEvent? = null,
    val isEditMode: Boolean = false
)

fun AddPlaceUiState.toHappyPlace(): HappyPlaceEntity? {
    if (this.latitude == null || this.longitude == null) {
        return null
    }
    return HappyPlaceEntity(
        id = this.id,
        title = this.title,
        image = this.imageUri,
        description = this.description,
        date = this.date,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun HappyPlaceEntity.toAddPlaceUiState(): AddPlaceUiState {
    return AddPlaceUiState(
        id = this.id,
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        date = this.date.orEmpty(),
        location = this.location.orEmpty(),
        latitude = this.latitude,
        longitude = this.longitude,
        imageUri = this.image,
        event = null,
        isEditMode = true
    )
}