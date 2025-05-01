package com.happyplaces.di

import com.happyplaces.data.datasource.remote.FirebaseService
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.data.datasource.remote.PlaceService
import org.koin.dsl.module

/** 網路層（Retrofit / OkHttp）定義 */
val networkModule = module {
    single<PlaceService> { FirebaseService() }
    single { PlaceRemoteDataSource(get()) }
}