package com.happyplaces.di

import com.happyplaces.database.HappyPlaceRepository
import com.happyplaces.database.HappyPlaceRepositoryImpl
import com.happyplaces.presentation.HappyPlaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Repository 提供
val repositoryModule = module {
    factory<HappyPlaceRepository> { HappyPlaceRepositoryImpl(get()) }
}

// ViewModel 提供
val viewModelModule = module {
    viewModel { HappyPlaceViewModel(androidContext(), get()) }
}