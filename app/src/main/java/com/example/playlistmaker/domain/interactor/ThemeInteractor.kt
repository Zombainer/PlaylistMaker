package com.example.playlistmaker.domain.interactor

interface ThemeInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}