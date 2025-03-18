package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {

    fun saveSearchHistory(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit().putString("search_history", json).apply()
    }

    fun loadSearchHistory(): List<Track> {
        val json = sharedPreferences.getString("search_history", null)
        return if (json != null) {
            Gson().fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }
    companion object {
        private const val DARK_THEME = "dark_theme"
    }

    var darkTheme: Boolean
        get() = sharedPreferences.getBoolean(DARK_THEME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(DARK_THEME, value).apply()
        }
}