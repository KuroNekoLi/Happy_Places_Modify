package com.happyplaces.util

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 觀察 LiveData，直到有值被設定後才回傳，或超時丟出例外。
 * 來源改自官方 Codelab 範例。&#8203;:contentReference[oaicite:0]{index=0}
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    // 開始觀察
    this.observeForever(observer)

    try {
        afterObserve.invoke()  // 觀察後可以額外觸發操作
        // 等待直到有值或超時
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        // 安全移除
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}