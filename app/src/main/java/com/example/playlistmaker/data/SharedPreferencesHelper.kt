package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PreferencesHelper
import com.google.gson.Gson

class SharedPreferencesHelper(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : PreferencesHelper {

    override fun saveSearchHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(Constants.SEARCH_HISTORY_KEY, json).apply()
    }

    override fun loadSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(Constants.SEARCH_HISTORY_KEY, null)
        return if (json != null) {
            gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override var darkTheme: Boolean
        get() = sharedPreferences.getBoolean(Constants.DARK_THEME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(Constants.DARK_THEME, value).apply()
        }
}