package com.example.playlistmaker.presentation.creator

import android.content.Context
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.data.network.ApiServiceImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.domain.interactor.ThemeInteractorImpl
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.interactor.TrackInteractorImpl
import com.google.gson.Gson

object Creator {

    private val gson = Gson() // Создаем экземпляр Gson

    fun provideTrackInteractor(context: Context): TrackInteractor {
        val apiService = ApiServiceImpl()
        val trackRepository = TrackRepositoryImpl(apiService)
        return TrackInteractorImpl(trackRepository)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences, gson) // Передаем gson
        val searchHistoryRepository = SearchHistoryRepositoryImpl(sharedPreferencesHelper)
        return SearchInteractorImpl(searchHistoryRepository)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences, gson) // Передаем gson
        return ThemeInteractorImpl(sharedPreferencesHelper)
    }
}