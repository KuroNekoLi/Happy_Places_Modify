package com.happyplaces.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/** 聚合所有 Module，並在 Application 啟動時呼叫 */
fun initKoin(app: Application) {
    startKoin {
        androidContext(app)
        modules(
            listOf(
                databaseModule,
                repositoryModule,
                viewModelModule,
                networkModule
            )
        )
    }
}