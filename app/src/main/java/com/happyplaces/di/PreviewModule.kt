package com.happyplaces.di

import com.happyplaces.data.repository.HappyPlaceRepository
import com.happyplaces.data.repository.MockHappyPlaceRepository
import com.happyplaces.presentation.HappyPlaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val previewModule = module() {
    single<HappyPlaceRepository> { MockHappyPlaceRepository() }
    viewModel { HappyPlaceViewModel(androidContext(), get()) }
}
