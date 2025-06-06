package com.happyplaces

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.happyplaces.di.initKoin
import okio.Path.Companion.toOkioPath
import java.io.File

class App : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        // 手動初始化 Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        // 啟用 App Events（選用）
        AppEventsLogger.activateApp(this)

        initKoin(this)
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // 使用 25% 的可用記憶體
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache").toOkioPath())
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                    .build()
            }
            .build()
    }
}
