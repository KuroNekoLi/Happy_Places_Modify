package com.happyplaces.domain.usecase

import com.happyplaces.data.model.AddPlaceUiState
import com.happyplaces.data.model.FormValidationResult
import com.happyplaces.data.model.validateForm

class ValidateFormUseCase {
    operator fun invoke(uiState: AddPlaceUiState): FormValidationResult {
        return uiState.validateForm()
    }
}

class ValidateFieldUseCase {
    fun validateTitle(title: String): String? {
        return if (title.isBlank()) "Please enter title" else null
    }

    fun validateDescription(description: String): String? {
        return if (description.isBlank()) "Please enter description" else null
    }

    fun validateLocation(location: String): String? {
        return if (location.isBlank()) "Please select location" else null
    }

    fun validateImage(imageUrl: String?): String? {
        return if (imageUrl.isNullOrBlank()) "Please add image" else null
    }
}