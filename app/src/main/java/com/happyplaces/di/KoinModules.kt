package com.happyplaces.di

import androidx.room.Room
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.local.UserDatabase
import com.happyplaces.data.repository.HappyPlaceRepository
import com.happyplaces.data.repository.HappyPlaceRepositoryImpl
import com.happyplaces.presentation.HappyPlaceViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "happy_place_data_database"
        ).build()
    }
    // DAO
    single<UserDao> { get<UserDatabase>().dao }

    // Repository
    factory<HappyPlaceRepository> { HappyPlaceRepositoryImpl(get()) }

    // ViewModel：Koin 會自動幫你產生 Factory
    viewModel { HappyPlaceViewModel(androidApplication(), get()) }
}
