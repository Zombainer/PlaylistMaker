package com.example.playlistmaker

import java.io.Serializable

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val album: String,
    val genre: String,
    val year: String,
    val duration: String,
    val country: String
): Serializable
