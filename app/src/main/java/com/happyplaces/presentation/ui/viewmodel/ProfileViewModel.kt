package com.happyplaces.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.usecase.GetCurrentUserUseCase
import com.happyplaces.domain.usecase.GetMyPlacesUseCase
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val currentUserUseCase: GetCurrentUserUseCase,
    private val getMyPlacesUseCase: GetMyPlacesUseCase
) : ViewModel() {
    val myPlaces = getMyPlacesUseCase.invoke()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            ApiResource.Loading()
        )
}