package com.example.playlistmaker.presentation.creator

import android.content.Context
import com.example.playlistmaker.data.network.ApiServiceImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.interactor.TrackInteractorImpl
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.data.repository.TrackRepositoryImpl

object Creator {

    fun provideTrackInteractor(context: Context): TrackInteractor {
        val apiService = ApiServiceImpl() // Создаем ApiService
        val trackRepository = TrackRepositoryImpl(apiService) // Передаем ApiService в TrackRepositoryImpl
        return TrackInteractorImpl(trackRepository)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        return SearchInteractorImpl(sharedPreferencesHelper)
    }
}