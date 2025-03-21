package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface PreferencesHelper {
    fun saveSearchHistory(tracks: List<Track>)
    fun loadSearchHistory(): List<Track>
    var darkTheme: Boolean
}