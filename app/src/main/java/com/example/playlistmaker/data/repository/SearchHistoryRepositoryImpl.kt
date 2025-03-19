package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(private val sharedPreferencesHelper: SharedPreferencesHelper) : SearchHistoryRepository {
    override fun saveSearchHistory(tracks: List<Track>) {
        sharedPreferencesHelper.saveSearchHistory(tracks)
    }

    override fun loadSearchHistory(): List<Track> {
        return sharedPreferencesHelper.loadSearchHistory()
    }
}