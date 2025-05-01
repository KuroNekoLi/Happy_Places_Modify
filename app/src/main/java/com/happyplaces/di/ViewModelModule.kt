package com.happyplaces.di

import com.happyplaces.presentation.HappyPlaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// ViewModel 提供
val viewModelModule = module {
    viewModel { HappyPlaceViewModel(androidContext(), get()) }
}