package com.happyplaces.util

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 日期選擇器工具類
 */
object DatePickerUtils {

    /**
     * 顯示日期選擇器對話框
     * @param context 上下文
     * @param onDateSelected 日期選擇回調
     */
    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val dateString = formatter.format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                onDateSelected(dateString)
            },
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        ).show()
    }
}