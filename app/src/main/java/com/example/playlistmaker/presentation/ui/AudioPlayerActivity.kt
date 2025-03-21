package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

class AudioPlayerActivity : AppCompatActivity() {

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
    private var mediaPlayer: MediaPlayer? = null
    private var handler = Handler()
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
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                timeTextView.text = formatTime(currentPosition)
                handler.postDelayed(this, 1000)
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
        val playerBackButton: Button = findViewById(R.id.playerBack_button)

        playerBackButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        // Получаем трек из Intent
        val track = intent.getSerializableExtra("TRACK_EXTRA") as Track
        displayTrackInfo(track)

        // Подготавливаем MediaPlayer для воспроизведения трека
        preparePlayer(track.previewUrl)
    }

    private fun displayTrackInfo(track: Track) {
        trackTitleTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumTextView.text = track.album
        genreTextView.text = track.genre
        yearTextView.text = track.year
        countryTextView.text = track.country
        durationTextView.text = track.duration

        // Загрузка обложки альбома с помощью Glide
        val artworkUrl100 = track.artworkUrl100
        val artworkUrl512 = artworkUrl100.replace("100x100bb", "512x512bb")

        Glide.with(this)
            .load(artworkUrl512)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.track_placeholder)
                    .error(R.drawable.track_placeholder)
                    .fitCenter()
                    .centerCrop()
            )
            .into(albumCoverImageView)
    }

    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl != null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playButton.isEnabled = true
                    playerState = STATE_PREPARED
                }
                setOnCompletionListener {
                    playerState = STATE_PREPARED
                    handler.removeCallbacks(updateTimeRunnable)
                    playButton.setImageResource(R.drawable.play_icon)
                    timeTextView.text = formatTime(0)
                }
            }
        } else {
            // Обработка случая, если previewUrl отсутствует
            playButton.isEnabled = false
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause_icon)
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_icon)
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
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
        mediaPlayer?.release()
        handler.removeCallbacks(updateTimeRunnable)
    }
}