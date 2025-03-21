package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(tracks: List<Track>)
    fun loadSearchHistory(): List<Track>
}