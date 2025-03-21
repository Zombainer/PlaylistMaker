package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ApiResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceImpl : ApiService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun search(query: String): Call<ApiResponse> {
        return apiService.search(query)
    }
}