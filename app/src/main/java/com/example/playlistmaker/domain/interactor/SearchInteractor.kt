package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchInteractor {
    fun addToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun moveToTopOfHistory(track: Track)
}