package com.happyplaces.util

import android.net.Uri
import androidx.core.net.toUri


suspend fun String.resolveUri(
    onLocal: suspend (Uri) -> String,
    onRemote: suspend (String) -> String
): String {
    val uri = this.toUri()
    val isLocal = uri.scheme == "content" || uri.scheme == "file"
    return if (isLocal) {
        onLocal(uri)
    } else {
        onRemote(this)
    }
}