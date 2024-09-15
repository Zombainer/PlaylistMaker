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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.TextView


class Search : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация EditText для ввода текста
        editText = findViewById(R.id.searchEditText)

        // Кнопка "Назад"
        val searchBackButton: Button = findViewById(R.id.searchBack_button)
        // Кнопка для очистки ввода
        val clearButton: ImageView = findViewById(R.id.clearSearchButton)
        // Инициализация placeholderImage, placeholderText и updateButton
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)

        updateButton.setOnClickListener {
            performSearch(searchText)
        }

        // Назначаем обработчик нажатия на кнопку "Назад"
        searchBackButton.setOnClickListener {
            finish() // Закрываем текущую активность
        }

        // Инициализация RecyclerView для отображения списка треков
        recyclerView = findViewById(R.id.recyclerView)
        trackAdapter = TrackAdapter() // Инициализируем адаптер для треков с пустым списком

        // Установка LayoutManager и адаптера для RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        // Настройка значка поиска в EditText
        val searchIcon: Drawable? = getDrawable(R.drawable.search_icon)
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(searchIcon, null, null, null)
        editText.compoundDrawablePadding = 16

        // Настройка TextWatcher для EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Этот метод не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Этот метод не используется
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
        if (!isNetworkAvailable()) {
            emptyPlaceholder(getString(R.string.internet_error),R.drawable.internet_error,true)// Показать заглушку при отсутствии интернета
            return
        }

        // Создание экземпляра Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создание сервиса API
        val apiService = retrofit.create(ApiService::class.java)

        // Выполнение запроса поиска
        apiService.search(query).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    handleResponse(response.body()!!) // Обработка успешного ответа
                } else {
                    emptyPlaceholder(getString(R.string.nothing),R.drawable.search_error,false) // Показать пустую заглушку при ошибке
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                emptyPlaceholder(getString(R.string.nothing), R.drawable.search_error, false) // Обработка ошибки
            }
        })
    }

    private fun handleResponse(apiResponse: ApiResponse) {
        if (apiResponse.resultCount == 0) {
            emptyPlaceholder(getString(R.string.nothing),R.drawable.search_error,false) // Если нет результатов, показать заглушку
        } else {
            val tracks = apiResponse.results.map { result ->
                Track(
                    trackName = result.trackName,
                    artistName = result.artistName,
                    trackTimeMillis = result.trackTimeMillis,
                    artworkUrl100 = result.artworkUrl100
                )
            }
            if (tracks.isEmpty()) {
                emptyPlaceholder(getString(R.string.nothing), R.drawable.search_error, false)
            } else {
                trackAdapter.updateTracks(tracks) // Обновляем треки в адаптере
                hidePlaceholder() // Скрываем заглушку, если результаты есть
                trackAdapter.notifyDataSetChanged() // Уведомляем адаптер об изменениях
            }
        }
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
        searchText = savedInstanceState.getString("searchText", "") // Восстанавливаем текст поиска из Bundle
    }
}
