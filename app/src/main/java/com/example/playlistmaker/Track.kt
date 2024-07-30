package com.example.playlistmaker

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: Long, // Измените на trackTimeMillis
    val artworkUrl100: String
)
