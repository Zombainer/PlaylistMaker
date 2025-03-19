package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.SharedPreferencesHelper

class ThemeInteractorImpl(private val sharedPreferencesHelper: SharedPreferencesHelper) : ThemeInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferencesHelper.darkTheme
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPreferencesHelper.darkTheme = darkThemeEnabled
    }
}