package com.happyplaces.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDateString(): String {
    val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
    return dateFormat.format(Date(this))
}