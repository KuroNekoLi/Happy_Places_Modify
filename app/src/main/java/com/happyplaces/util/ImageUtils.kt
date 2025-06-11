package com.happyplaces.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.happyplaces.BuildConfig
import java.io.File

/**
 * 圖片處理工具類
 */
object ImageUtils {

    /**
     * 建立臨時檔案 URI 用於相機拍照
     * @param context 上下文
     * @return 臨時檔案的 URI
     */
    fun buildFileUri(context: Context): Uri {
        val storage = if (
            android.os.Environment.MEDIA_MOUNTED == android.os.Environment.getExternalStorageState()
        ) {
            context.externalCacheDir
        } else {
            context.cacheDir
        }

        val file = File.createTempFile("tmp_img", ".jpg", storage).apply {
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }
}