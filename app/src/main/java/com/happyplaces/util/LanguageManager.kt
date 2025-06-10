package com.happyplaces.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * 語言管理工具類
 * 處理應用程式的多語言切換功能
 */
object LanguageManager {

    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    // 支援的語言選項
    enum class Language(val code: String, val displayName: String) {
        SYSTEM("system", "Follow System / 跟隨系統"),
        TRADITIONAL_CHINESE("zh-TW", "繁體中文"),
        ENGLISH("en", "English")
    }

    /**
     * 取得 SharedPreferences
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 儲存選擇的語言
     */
    fun saveLanguage(context: Context, language: Language) {
        getPrefs(context).edit()
            .putString(KEY_LANGUAGE, language.code)
            .apply()
    }

    /**
     * 取得儲存的語言設定
     */
    fun getSavedLanguage(context: Context): Language {
        val savedCode = getPrefs(context).getString(KEY_LANGUAGE, Language.SYSTEM.code)
        return Language.values().find { it.code == savedCode } ?: Language.SYSTEM
    }

    /**
     * 應用語言設定
     */
    fun applyLanguage(context: Context, language: Language) {
        saveLanguage(context, language)

        when (language) {
            Language.SYSTEM -> {
                // 跟隨系統語言
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }

            Language.TRADITIONAL_CHINESE -> {
                // 設定為繁體中文
                val locale = Locale("zh", "TW")
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
            }

            Language.ENGLISH -> {
                // 設定為英文
                val locale = Locale("en")
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
            }
        }
    }

    /**
     * 取得目前系統語言
     */
    fun getCurrentSystemLanguage(context: Context): Language {
        val currentLocale = getCurrentLocale(context)
        return when {
            currentLocale.language == "zh" && (currentLocale.country == "TW" || currentLocale.country == "HK") -> {
                Language.TRADITIONAL_CHINESE
            }

            else -> Language.ENGLISH
        }
    }

    /**
     * 取得目前的 Locale
     */
    private fun getCurrentLocale(context: Context): Locale {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    /**
     * 判斷是否為繁體中文環境
     */
    fun isTraditionalChinese(context: Context): Boolean {
        val currentLocale = getCurrentLocale(context)
        return currentLocale.language == "zh" &&
                (currentLocale.country == "TW" || currentLocale.country == "HK")
    }

    /**
     * 初始化語言設定（在 Application 中呼叫）
     */
    fun initializeLanguage(context: Context) {
        val savedLanguage = getSavedLanguage(context)
        if (savedLanguage != Language.SYSTEM) {
            applyLanguage(context, savedLanguage)
        }
    }
}
