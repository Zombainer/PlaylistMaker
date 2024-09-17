package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory {

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
    }

    private val tracks: MutableList<Track> = mutableListOf()

    fun addTrack(track: Track) {
        // Удаляем трек из списка, если он уже есть
        tracks.remove(track)
        // Добавляем трек в начало списка
        tracks.add(0, track)
        // Ограничиваем размер списка до 10
        if (tracks.size > 10) {
            tracks.removeAt(tracks.size - 1)
        }
    }
    fun saveToPreferences(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tracks)  // Преобразуем список треков в JSON-формат
        editor.putString(SEARCH_HISTORY_KEY, json)
        editor.apply()
    }

    fun loadFromPreferences(sharedPreferences: SharedPreferences) {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            val loadedTracks: List<Track> = Gson().fromJson(json, type)
            tracks.clear()
            tracks.addAll(loadedTracks)
        }
    }

    fun getTracks(): List<Track> {
        return tracks
    }

    fun clear() {
        tracks.clear()
    }
}
