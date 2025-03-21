package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.ApiResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.ApiService
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val apiService: ApiService) : TrackRepository {

    private val trackMapper = TrackMapper()

    override fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        apiService.search(query).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val tracks = response.body()!!.results.map { trackMapper.map(it) }
                    callback(tracks)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}