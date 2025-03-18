package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.ApiService
import com.example.playlistmaker.data.dto.ApiResponse
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call

class TrackRepositoryImpl(private val apiService: ApiService) : TrackRepository {

    override fun searchTracks(query: String): Call<ApiResponse> {
        return apiService.search(query) // Возвращаем Call<ApiResponse>
    }
}