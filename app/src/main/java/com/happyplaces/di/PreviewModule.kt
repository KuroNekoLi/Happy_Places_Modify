package com.happyplaces.di

import com.happyplaces.data.repository.MockHappyPlaceRepository
import com.happyplaces.domain.repository.HappyPlaceRepository
import com.happyplaces.domain.usecase.GetMyIdUseCase
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val previewModule = module() {
    single<HappyPlaceRepository> { MockHappyPlaceRepository() }
    factory { GetMyIdUseCase(get()) }
    viewModel { HappyPlaceViewModel(androidContext(), get(), get()) }
}
