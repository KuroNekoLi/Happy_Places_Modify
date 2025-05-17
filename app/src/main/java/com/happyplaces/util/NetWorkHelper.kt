package com.happyplaces.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat

// 檢查當前是否有可用且被系統驗證過的網路（可連外部 Internet）
private fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = ContextCompat.getSystemService(
        context,
        ConnectivityManager::class.java
    ) as ConnectivityManager
    // 取得系統認定的「目前」網路
    val activeNetwork: Network = connectivityManager.activeNetwork ?: return false
    // 查詢此網路的能力
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
