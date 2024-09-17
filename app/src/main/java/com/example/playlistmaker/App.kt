package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        private const val APP_PREFERENCE = "app_preferences"
        private const val DARK_THEME = "dark_theme"
    }

    private lateinit var sharedPreferences: SharedPreferences
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        // Инициализируем SharedPreferences
        sharedPreferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)

        // Получаем сохранённое значение темы из SharedPreferences
        darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)

        // Применяем тему на основании сохранённого значения
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Изменяем режим ночной темы
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        // Сохраняем текущее значение темы в SharedPreferences
        sharedPreferences.edit().putBoolean(DARK_THEME, darkTheme).apply()
    }
}

