package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.SharedPreferencesHelper

class ThemeInteractor(private val sharedPreferencesHelper: SharedPreferencesHelper) {

    fun isDarkThemeEnabled(): Boolean {
        return sharedPreferencesHelper.darkTheme
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPreferencesHelper.darkTheme = darkThemeEnabled
    }
}