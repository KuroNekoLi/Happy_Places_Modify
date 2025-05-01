package com.happyplaces.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.happyplaces.di.previewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

@Composable
fun SetupPreviewKoin(module: Module = previewModule) {
    val context = LocalContext.current
    remember {
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(context)
                // 允許覆寫既有定義
                allowOverride(true)
                // 僅載入 Preview 模組
                modules(module)
            }
        } else {
            // 已啟動則動態載入
            loadKoinModules(module)
        }
        true
    }
}