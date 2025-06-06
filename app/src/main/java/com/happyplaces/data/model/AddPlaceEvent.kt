package com.happyplaces.data.model

sealed class AddPlaceEvent {
    data object ShowDatePicker : AddPlaceEvent()
    data object ShowImagePicker : AddPlaceEvent()
    data object ShowPlacesAutocomplete : AddPlaceEvent()
    data object RequestCurrentLocation : AddPlaceEvent()
    data class ShowToast(val message: String) : AddPlaceEvent()
    data object NavigateBack : AddPlaceEvent()
    data class ShowError(val message: String) : AddPlaceEvent()
    data object ShowLoading : AddPlaceEvent()
    data object HideLoading : AddPlaceEvent()
}
