package com.happyplaces.di

import com.happyplaces.data.datasource.remote.FirebaseAuthProvider
import com.happyplaces.data.datasource.remote.UserRepositoryImpl
import com.happyplaces.data.repository.HappyPlaceRepositoryImpl
import com.happyplaces.domain.AuthProvider
import com.happyplaces.domain.HappyPlaceRepository
import com.happyplaces.domain.UserRepository
import org.koin.dsl.module

/** 資料層（Repository）綁定 */
val repositoryModule = module {
    factory<HappyPlaceRepository> {
        HappyPlaceRepositoryImpl(
            placeRemoteDataSource = get(),
            dao = get()
        )
    }
    factory<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthProvider> { FirebaseAuthProvider() }
}