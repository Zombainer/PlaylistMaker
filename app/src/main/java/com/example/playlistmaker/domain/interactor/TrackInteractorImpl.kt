package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        trackRepository.searchTracks(query, callback)
    }
}