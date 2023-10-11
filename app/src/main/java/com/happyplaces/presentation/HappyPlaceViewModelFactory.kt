package com.happyplaces.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happyplaces.database.HappyPlaceRepository

class HappyPlaceViewModelFactory(
    private val application: Application,
    private val repository: HappyPlaceRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HappyPlaceViewModel::class.java)) {
            return HappyPlaceViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}