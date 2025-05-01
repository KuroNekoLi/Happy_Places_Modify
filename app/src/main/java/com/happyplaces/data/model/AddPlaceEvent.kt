package com.happyplaces.data.model

sealed class AddPlaceEvent {
    object ShowDatePicker : AddPlaceEvent()
    object ShowImagePicker : AddPlaceEvent()
    object ShowPlacesAutocomplete : AddPlaceEvent()
    object RequestCurrentLocation : AddPlaceEvent()
    data class ShowToast(val message: String) : AddPlaceEvent()
    object NavigateBack : AddPlaceEvent()
}