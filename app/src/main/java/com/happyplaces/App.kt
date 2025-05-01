package com.happyplaces

import android.app.Application
import com.happyplaces.di.initKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}