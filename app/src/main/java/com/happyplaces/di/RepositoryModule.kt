package com.happyplaces.di

import com.happyplaces.data.repository.HappyPlaceRepositoryImpl
import com.happyplaces.domain.HappyPlaceRepository
import org.koin.dsl.module

/** 資料層（Repository）綁定 */
val repositoryModule = module {
    factory<HappyPlaceRepository> {
        HappyPlaceRepositoryImpl(
            placeRemoteDataSource = get(),
            dao = get()
        )
    }
}