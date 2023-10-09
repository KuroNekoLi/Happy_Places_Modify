package com.happyplaces.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happyplaces.database.HappyPlaceRepository

class HappyPlaceViewModelFactory(private val repository: HappyPlaceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HappyPlaceViewModel::class.java)) {
            return HappyPlaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}