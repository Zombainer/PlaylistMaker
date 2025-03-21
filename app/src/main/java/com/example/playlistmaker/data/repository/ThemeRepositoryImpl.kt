package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferencesHelper: SharedPreferencesHelper) : ThemeRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferencesHelper.darkTheme
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferencesHelper.darkTheme = enabled
    }
}