package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.dto.ApiResponse
import retrofit2.Call

interface TrackInteractor {
    fun searchTracks(query: String): Call<ApiResponse> // Возвращаем Call<ApiResponse>
}