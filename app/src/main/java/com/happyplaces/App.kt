package com.happyplaces

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.happyplaces.di.initKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 手動初始化 Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        // 啟用 App Events（選用）
        AppEventsLogger.activateApp(this)

        initKoin(this)
    }
}