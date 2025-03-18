package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.dto.ApiResponse
import retrofit2.Call

interface TrackRepository {
    fun searchTracks(query: String): Call<ApiResponse> // Возвращаем Call<ApiResponse>
}