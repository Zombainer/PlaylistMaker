package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) : SearchInteractor {

    override fun addToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.remove(track)
        history.add(0, track)
        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }
        searchHistoryRepository.saveSearchHistory(history)
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistoryRepository.loadSearchHistory()
    }

    override fun clearSearchHistory() {
        searchHistoryRepository.saveSearchHistory(emptyList())
    }

    override fun moveToTopOfHistory(track: Track) {
        addToHistory(track)
    }
}