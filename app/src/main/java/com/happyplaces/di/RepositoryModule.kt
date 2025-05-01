package com.happyplaces.di

import com.happyplaces.data.repository.HappyPlaceRepository
import com.happyplaces.data.repository.HappyPlaceRepositoryImpl
import org.koin.dsl.module

/** 資料層（Repository）綁定 */
val repositoryModule = module {
    // 每次注入都會拿到新的實例，若要改為單例請改用 single{}
    factory<HappyPlaceRepository> { HappyPlaceRepositoryImpl(get()) }
}