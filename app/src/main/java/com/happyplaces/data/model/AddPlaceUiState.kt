package com.happyplaces.data.model

import com.happyplaces.domain.model.HappyPlace

data class AddPlaceUiState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null,
    val event: AddPlaceEvent? = null,
    val isEditMode: Boolean = false
)

fun AddPlaceUiState.toHappyPlace(): HappyPlace? {
    if (this.latitude == null || this.longitude == null) {
        return null
    }
    return HappyPlace(
        id = this.id,
        title = this.title,
        image = this.imageUrl,
        description = this.description,
        date = this.date,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
        creatorId = ""
    )
}

fun HappyPlace.toAddPlaceUiState(): AddPlaceUiState {
    return AddPlaceUiState(
        id = this.id,
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        date = this.date.orEmpty(),
        location = this.location.orEmpty(),
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.image,
        event = null,
        isEditMode = true
    )
}