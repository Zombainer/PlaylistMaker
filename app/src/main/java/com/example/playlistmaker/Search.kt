package com.example.playlistmaker

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import android.view.inputmethod.EditorInfo
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import android.os.Handler

class Search : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryTitle: TextView
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var searchRunnable: Runnable
    private var currentCall: Call<ApiResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editText = findViewById(R.id.searchEditText)


        val searchBackButton: Button = findViewById(R.id.searchBack_button)
        val clearButton: ImageView = findViewById(R.id.clearSearchButton)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)

        searchHistory = SearchHistory()  // Инициализация списка истории поиска
        searchHistory.loadFromPreferences(getSharedPreferences("AppPreferences", Context.MODE_PRIVATE))

        trackAdapter = TrackAdapter()
        historyAdapter = HistoryAdapter()
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        searchRunnable = Runnable {
            if (searchText.isNotEmpty()) {
                performSearch(searchText)
            } else {
                updateUIBasedOnSearchText()
            }
        }

        updateButton.setOnClickListener {
            performSearch(searchText)
        }

        searchBackButton.setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        val searchIcon: Drawable? = getDrawable(R.drawable.search_icon)
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(searchIcon, null, null, null)
        editText.compoundDrawablePadding = 16

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchHandler.removeCallbacks(searchRunnable) // Удаляем предыдущий Runnable
                searchText = s.toString() // Обновляем текст поиска
                searchHandler.postDelayed(searchRunnable, 300)
            }

            override fun afterTextChanged(editable: Editable?) {
                searchText = editable?.toString() ?: "" // Сохраняем текущий текст поиска
                clearButton.visibility = if (editable.isNullOrEmpty()) {
                    View.INVISIBLE // Если текст пустой, скрываем кнопку очистки
                } else {
                    View.VISIBLE // Иначе, показываем кнопку очистки
                }

            }

        })

        // Обработка фокуса EditText
        editText.setOnFocusChangeListener { _, hasFocus ->
            clearButton.visibility = if (hasFocus && editText.text.isNotEmpty()) {
                View.VISIBLE // Если EditText в фокусе и не пустой, показываем кнопку очистки
            } else {
                View.INVISIBLE // Иначе скрываем кнопку очистки
            }
        }

        // Обработка нажатия на кнопку очистки
        clearButton.setOnClickListener {
            editText.text.clear() // Очищаем текст в EditText
            clearButton.visibility = View.INVISIBLE // Скрываем кнопку очистки
            hideKeyboard() // Скрываем клавиатуру
            trackAdapter.clearTracks()
            toggleHistoryVisibility()
            hidePlaceholder()
            recyclerView.visibility = View.GONE
        }

        // Восстановление текста поиска из savedInstanceState
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
            editText.setText(searchText)
        }

        // Обработка нажатия на клавишу "Done" в клавиатуре
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(editText.text.toString()) // Выполняем поиск
                hideKeyboard() // Скрываем клавиатуру
                true
            } else {
                false
            }
        }
        trackAdapter.setOnTrackClickListener { track ->
            addToHistory(track)
            val intent = Intent(this, AudioPlayer::class.java)
            intent.putExtra("TRACK_EXTRA", track) // Отправляем трек
            startActivity(intent) // Открываем плеер
            Toast.makeText(this, "Трек добавлен в историю", Toast.LENGTH_SHORT).show()
        }
        toggleHistoryVisibility()
    }

    private fun updateUIBasedOnSearchText() {
        if (searchText.isEmpty()) {
            // Показать историю, если текст поиска пустой
            toggleHistoryVisibility()
            recyclerView.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderText.visibility = View.GONE
            updateButton.visibility = View.GONE
        } else {
            // Если есть текст, скрыть историю и выполнить поиск
            historyUInvisible()
            performSearch(searchText)
        }
    }

    private fun clearSearchHistory() {
        searchHistory.clear() // Очистка истории
        searchHistory.saveToPreferences(getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)) // Сохранение изменений
        toggleHistoryVisibility() // Обновление отображения истории
        historyAdapter.clearHistory()
    }
    private fun addToHistory(track: Track) {
        searchHistory.addTrack(track) // Добавляем трек в историю
        searchHistory.saveToPreferences(getSharedPreferences("AppPreferences", Context.MODE_PRIVATE))
    }

    private fun historyUInvisible(){
        searchHistoryTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
    }

    private fun toggleHistoryVisibility() {
        val history = searchHistory.getTracks()
        if (history.isNotEmpty()) {
            searchHistoryTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
            historyAdapter.updateHistory(history) // Используем historyAdapter
            historyAdapter.setOnTrackClickListener { track ->
                val intent = Intent(this, AudioPlayer::class.java)
                intent.putExtra("TRACK_EXTRA", track) // Отправляем трек
                startActivity(intent) // Открываем плеер
                moveToTopOfHistory(track) // Перемещение трека на первое место
                toggleHistoryVisibility() // Обновление отображения
            }
        } else {
            historyUInvisible()
        }
    }

    private fun moveToTopOfHistory(track: Track) {
        searchHistory.addTrack(track) // Добавляем трек в историю (он будет перемещён на первое место)
        searchHistory.saveToPreferences(getSharedPreferences("AppPreferences", Context.MODE_PRIVATE))
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

        if (currentCall != null) {
            currentCall?.cancel() // Отмена текущего запроса
        }

        if (!isNetworkAvailable()) {
            emptyPlaceholder(getString(R.string.internet_error),R.drawable.internet_error,true)// Показать заглушку при отсутствии интернета
            historyUInvisible()
            return
        }
        historyUInvisible()
        recyclerView.visibility = View.GONE

        // Создание экземпляра Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создание сервиса API
        val apiService = retrofit.create(ApiService::class.java)

        // Выполнение запроса поиска
        currentCall = apiService.search(query)
        currentCall?.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    handleResponse(response.body()!!) // Обработка успешного ответа
                } else {
                    emptyPlaceholder(getString(R.string.nothing),R.drawable.search_error,false) // Показать пустую заглушку при ошибке
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {

            }
        })
    }

    private fun handleResponse(apiResponse: ApiResponse) {
        if (apiResponse.resultCount == 0) {
            emptyPlaceholder(getString(R.string.nothing),R.drawable.search_error,false) // Если нет результатов, показать заглушку
            historyUInvisible()
        } else {

            val tracks = apiResponse.results.map { result ->
                Track(
                    trackName = result.trackName,
                    artistName = result.artistName,
                    trackTimeMillis = result.trackTimeMillis,
                    artworkUrl100 = result.artworkUrl100,
                    album = result.collectionName ?: "", // Название альбома
                    genre = result.primaryGenreName ?: "", // Жанр
                    year = result.releaseDate?.substring(0, 4) ?: "", // Год релиза
                    duration = formatDuration(result.trackTimeMillis ?: 0), // Длительность
                    country = result.country ?: "" // Страна
                )
            }
            if (tracks.isEmpty()) {
                emptyPlaceholder(getString(R.string.nothing), R.drawable.search_error, false)
                historyUInvisible()
            } else {
                trackAdapter.updateTracks(tracks) // Обновляем треки в адаптере
                hidePlaceholder() // Скрываем заглушку, если результаты есть
                trackAdapter.notifyDataSetChanged() // Уведомляем адаптер об изменениях
            }
        }
    }

    private fun formatDuration(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun emptyPlaceholder(message: String, imageResId: Int, showUpdateButton: Boolean) {
        recyclerView.visibility = View.GONE
        placeholderImage.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE
        placeholderText.text = message
        placeholderImage.setImageResource(imageResId)

        if (showUpdateButton) {
            updateButton.visibility = View.VISIBLE
        } else {
            updateButton.visibility = View.GONE
        }
    }

    private fun hidePlaceholder() {
        recyclerView.visibility = View.VISIBLE
        placeholderImage.visibility = View.GONE
        placeholderText.visibility = View.GONE
        updateButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0) // Скрываем клавиатуру
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchText", searchText) // Сохраняем текст из EditText в Bundle
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "")
        toggleHistoryVisibility()// Восстанавливаем текст поиска из Bundle
    }
}