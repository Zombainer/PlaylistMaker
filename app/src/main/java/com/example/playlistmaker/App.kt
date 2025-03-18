package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class App : Application() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var themeInteractor: ThemeInteractor // Делаем themeInteractor публичным

    override fun onCreate() {
        super.onCreate()

        sharedPreferencesHelper = SharedPreferencesHelper(
            getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        )
        themeInteractor = ThemeInteractor(sharedPreferencesHelper)

        // Применяем тему на основании сохранённого значения
        switchTheme(themeInteractor.isDarkThemeEnabled())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeInteractor.switchTheme(darkThemeEnabled)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}