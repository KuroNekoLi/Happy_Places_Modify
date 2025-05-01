package com.happyplaces.di

import androidx.room.Room
import com.happyplaces.data.datasource.local.UserDao
import com.happyplaces.data.datasource.local.UserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // 建立 Room Database 實例
    single {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "happy_place_data_database"

        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
    // DAO
    single<UserDao> { get<UserDatabase>().dao }
}