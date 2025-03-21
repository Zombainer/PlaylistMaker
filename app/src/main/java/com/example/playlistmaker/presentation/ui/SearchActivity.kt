package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.SearchAdapter
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.creator.Creator

class SearchActivity : AppCompatActivity() {

    private lateinit var trackInteractor: TrackInteractor
    private lateinit var searchInteractor: SearchInteractor

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: SearchAdapter

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация интеракторов
        trackInteractor = Creator.provideTrackInteractor(this)
        searchInteractor = Creator.provideSearchInteractor(this)

        // Инициализация View
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearSearchButton)
        recyclerView = findViewById(R.id.recyclerView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)
        progressBar = findViewById(R.id.progressBar)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Настройка RecyclerView для результатов поиска
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(emptyList()) { track ->
            searchInteractor.addToHistory(track)
            openAudioPlayer(track)
        }
        recyclerView.adapter = trackAdapter

        // Настройка RecyclerView для истории поиска
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(emptyList()) { track ->
            searchInteractor.moveToTopOfHistory(track)
            openAudioPlayer(track)
        }
        historyRecyclerView.adapter = searchAdapter

        // Обработка ввода текста в поисковой строке
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchHandler.removeCallbacks(searchRunnable)
                searchRunnable = Runnable {
                    if (s.isNullOrEmpty()) {
                        updateUIBasedOnSearchText()
                    } else {
                        historyUInvisible()
                        performSearch(s.toString())
                    }
                }
                searchHandler.postDelayed(searchRunnable, 300)
            }

            override fun afterTextChanged(s: Editable?) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            }
        })

        // Очистка поисковой строки
        clearButton.setOnClickListener {
            toggleHistoryVisibility()
            searchEditText.text.clear()
            hideKeyboard()
        }

        // Кнопка "Назад"
        val searchBackButton: Button = findViewById(R.id.searchBack_button)
        searchBackButton.setOnClickListener {
            finish()
        }

        // Обновление результатов поиска
        updateButton.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        // Очистка истории поиска
        clearHistoryButton.setOnClickListener {
            searchInteractor.clearSearchHistory()
            toggleHistoryVisibility()
        }

        // Отображение истории поиска при запуске
        toggleHistoryVisibility()
    }

    override fun onResume() {
        super.onResume()
        updateUIBasedOnSearchText()
    }

    private fun updateUIBasedOnSearchText() {
        if (searchEditText.text.isNullOrEmpty()) {
            toggleHistoryVisibility()
            recyclerView.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderText.visibility = View.GONE
            updateButton.visibility = View.GONE
        } else {
            historyUInvisible()
            recyclerView.visibility = View.VISIBLE
        }
    }

    // Проверка интернет соединения
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            toggleHistoryVisibility()
            return
        }

        if (!isNetworkAvailable()) {
            showPlaceholder(getString(R.string.internet_error), R.drawable.internet_error, true)
            historyUInvisible()
            return
        }

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderText.visibility = View.GONE
        updateButton.visibility = View.GONE

        // Используем trackInteractor для выполнения поиска
        trackInteractor.searchTracks(query) { tracks ->
            progressBar.visibility = View.GONE
            if (tracks.isEmpty()) {
                showPlaceholder(getString(R.string.nothing), R.drawable.search_error, false)
                historyUInvisible()
            } else {
                trackAdapter.updateTracks(tracks)
                hidePlaceholder()
                trackAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun toggleHistoryVisibility() {
        val history = searchInteractor.getSearchHistory()
        if (history.isEmpty()) {
            historyUInvisible()
        } else {
            searchAdapter.updateTracks(history)
            searchHistoryTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun historyUInvisible() {
        searchHistoryTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
    }

    private fun showPlaceholder(message: String, imageResId: Int, showUpdateButton: Boolean) {
        placeholderImage.setImageResource(imageResId)
        placeholderText.text = message
        placeholderImage.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE
        updateButton.visibility = if (showUpdateButton) View.VISIBLE else View.GONE
    }

    private fun hidePlaceholder() {
        recyclerView.visibility = View.VISIBLE
        placeholderImage.visibility = View.GONE
        placeholderText.visibility = View.GONE
        updateButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("TRACK_EXTRA", track)
        startActivity(intent)
    }
}