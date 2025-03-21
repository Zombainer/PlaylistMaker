package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface TrackInteractor {
    fun searchTracks(query: String, callback: (List<Track>) -> Unit)
}