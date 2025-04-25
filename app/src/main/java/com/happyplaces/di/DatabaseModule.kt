package com.happyplaces.di

import androidx.room.Room
import com.happyplaces.database.UserDao
import com.happyplaces.database.UserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // 建立 Room Database 實例
    single {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "happy_place_data_database"
        ).build()
    }
    // DAO
    single<UserDao> { get<UserDatabase>().dao }
}