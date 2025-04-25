package com.happyplaces.di

import androidx.room.Room
import com.happyplaces.database.HappyPlaceRepository
import com.happyplaces.database.UserDao
import com.happyplaces.database.UserDatabase
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
    single { HappyPlaceRepository(get()) }

    // ViewModel：Koin 會自動幫你產生 Factory
    viewModel { HappyPlaceViewModel(androidApplication(), get()) }
}
