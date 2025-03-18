package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun toTrack(): Track {
        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            album = collectionName ?: "",
            genre = primaryGenreName ?: "",
            year = releaseDate?.substring(0, 4) ?: "",
            duration = formatDuration(trackTimeMillis),
            country = country ?: "",
            previewUrl = previewUrl ?: ""
        )
    }

    private fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}