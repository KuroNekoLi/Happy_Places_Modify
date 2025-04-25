package com.happyplaces

import android.app.Application
import com.happyplaces.di.databaseModule
import com.happyplaces.di.repositoryModule
import com.happyplaces.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                databaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}