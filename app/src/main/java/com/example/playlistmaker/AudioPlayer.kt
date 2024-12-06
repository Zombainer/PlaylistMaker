package com.example.playlistmaker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        val playerBack_button: Button = findViewById(R.id.playerBack_button)

        playerBack_button.setOnClickListener {
            finish()
        }

        trackTitleTextView = findViewById(R.id.TrackEdit)
        artistNameTextView = findViewById(R.id.GroupEdit)
        albumTextView = findViewById(R.id.AlbumEdit)
        genreTextView = findViewById(R.id.GenreEdit)
        yearTextView = findViewById(R.id.YearEdit)
        countryTextView = findViewById(R.id.CountryEdit)
        durationTextView = findViewById(R.id.DurationEdit)
        albumCoverImageView = findViewById(R.id.AlbumCoverEdit)
        timeTextView = findViewById(R.id.TimeEdit)

        val track = intent.getSerializableExtra("TRACK_EXTRA") as Track
        displayTrackInfo(track)
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
            .apply(
                RequestOptions()
                .placeholder(R.drawable.track_placeholder)
                .error(R.drawable.track_placeholder)
                .fitCenter()
                .centerCrop())
            .into(albumCoverImageView)
    }
}
