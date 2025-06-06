package com.happyplaces.data.model

import com.happyplaces.domain.model.HappyPlace

data class AddPlaceUiState(
    val id: String = "",
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val date: String = "",
    val location: String = "",
    val locationError: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null,
    val imageError: String? = null,
    val event: AddPlaceEvent? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false
) {
    val hasErrors: Boolean
        get() = titleError != null || descriptionError != null ||
                locationError != null || imageError != null
}

sealed class FormValidationResult {
    object Valid : FormValidationResult()
    data class Invalid(val errors: Map<String, String>) : FormValidationResult()
}

fun AddPlaceUiState.validateForm(): FormValidationResult {
    val errors = mutableMapOf<String, String>()

    if (title.isBlank()) {
        errors["title"] = "Please enter title"
    }

    if (description.isBlank()) {
        errors["description"] = "Please enter description"
    }

    if (location.isBlank()) {
        errors["location"] = "Please select location"
    }

    if (imageUrl.isNullOrBlank()) {
        errors["image"] = "Please add image"
    }

    return if (errors.isEmpty()) {
        FormValidationResult.Valid
    } else {
        FormValidationResult.Invalid(errors)
    }
}

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
