package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.dto.ApiResponse
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun searchTracks(query: String): Call<ApiResponse> {
        return trackRepository.searchTracks(query) // Возвращаем Call<ApiResponse>
    }
}