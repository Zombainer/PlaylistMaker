package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>) -> Unit)
}