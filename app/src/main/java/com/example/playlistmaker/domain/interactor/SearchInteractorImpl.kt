package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.model.Track

class SearchInteractorImpl(private val sharedPreferencesHelper: SharedPreferencesHelper) : SearchInteractor {

    override fun addToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.remove(track) // Удаляем трек, если он уже есть в истории
        history.add(0, track) // Добавляем трек на первое место
        if (history.size > 10) {
            history.removeAt(history.size - 1) // Ограничиваем размер истории
        }
        sharedPreferencesHelper.saveSearchHistory(history)
    }

    override fun getSearchHistory(): List<Track> {
        return sharedPreferencesHelper.loadSearchHistory()
    }

    override fun clearSearchHistory() {
        sharedPreferencesHelper.saveSearchHistory(emptyList())
    }

    override fun moveToTopOfHistory(track: Track) {
        addToHistory(track) // Просто вызываем addToHistory, чтобы переместить трек на первое место
    }
}