package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class AudioPlayer : AppCompatActivity() {

    private lateinit var trackTitleTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var albumCoverImageView: ImageView
    private lateinit var timeTextView: TextView
    private lateinit var playButton: ImageView
    private var mediaPlayer = MediaPlayer()
    private var handler = Handler()
    private var url: String? = null
    private var playerState = STATE_DEFAULT

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                timeTextView.text = formatTime(currentPosition)
                handler.postDelayed(this, 1000) // обновление каждую секунду
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        playButton = findViewById(R.id.PlayButton)
        trackTitleTextView = findViewById(R.id.TrackEdit)
        artistNameTextView = findViewById(R.id.GroupEdit)
        albumTextView = findViewById(R.id.AlbumEdit)
        genreTextView = findViewById(R.id.GenreEdit)
        yearTextView = findViewById(R.id.YearEdit)
        countryTextView = findViewById(R.id.CountryEdit)
        durationTextView = findViewById(R.id.DurationEdit)
        albumCoverImageView = findViewById(R.id.AlbumCoverEdit)
        timeTextView = findViewById(R.id.TimeEdit)
        val playerBack_button: Button = findViewById(R.id.playerBack_button)

        playerBack_button.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        val track = intent.getSerializableExtra("TRACK_EXTRA") as Track
        displayTrackInfo(track)

        // Получаем URL для воспроизведения трека
        fetchTrackUrl(track.trackName)
    }

    private fun displayTrackInfo(track: Track) {
        trackTitleTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumTextView.text = track.album
        genreTextView.text = track.genre
        yearTextView.text = track.year
        countryTextView.text = track.country
        durationTextView.text = track.duration

        val artworkUrl100 = track.artworkUrl100
        val artworkUrl512 = artworkUrl100.replace("100x100bb", "512x512bb")

        Glide.with(this)
            .load(artworkUrl512)
            .apply(RequestOptions()
                .placeholder(R.drawable.track_placeholder)
                .error(R.drawable.track_placeholder)
                .fitCenter()
                .centerCrop())
            .into(albumCoverImageView)
    }

    private fun fetchTrackUrl(trackName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.search(trackName).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.resultCount > 0) {
                            val result = apiResponse.results.first()
                            url = result.previewUrl // Используем previewUrl для воспроизведения
                            preparePlayer()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                t.printStackTrace() // Обработка ошибки
            }
        })
    }

    private fun preparePlayer() {
        if (url != null) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                handler.removeCallbacks(updateTimeRunnable)
                playButton.setImageResource(R.drawable.play_icon)
                timeTextView.text = formatTime(0)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause_icon)
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_icon)
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%01d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeRunnable)
    }
}
