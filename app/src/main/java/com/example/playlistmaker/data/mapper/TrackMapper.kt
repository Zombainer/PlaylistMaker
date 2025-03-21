package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

class TrackMapper {
    fun map(trackDto: TrackDto): Track {
        return Track(
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            trackTimeMillis = trackDto.trackTimeMillis,
            artworkUrl100 = trackDto.artworkUrl100,
            album = trackDto.collectionName ?: "",
            genre = trackDto.primaryGenreName ?: "",
            year = trackDto.releaseDate?.substring(0, 4) ?: "",
            duration = formatDuration(trackDto.trackTimeMillis),
            country = trackDto.country ?: "",
            previewUrl = trackDto.previewUrl ?: ""
        )
    }

    private fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}