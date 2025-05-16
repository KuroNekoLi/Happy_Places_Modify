package com.happyplaces.di

import com.happyplaces.presentation.ui.viewmodel.AuthViewModel
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import com.happyplaces.presentation.ui.viewmodel.OnboardingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// ViewModel 提供
val viewModelModule = module {
    viewModel { HappyPlaceViewModel(androidContext(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
}