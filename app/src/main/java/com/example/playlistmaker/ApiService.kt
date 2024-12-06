package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ApiResponse>
}

data class ApiResponse(
    val resultCount: Int,
    val results: List<Result>
)

data class Result(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val collectionName: String?,
    val artworkUrl100: String,
    val primaryGenreName: String?,      // Добавлено поле
    val releaseDate: String?,            // Добавлено поле
    val duration: String,
    val country: String?
)