package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artwork)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        val formattedTime = dateFormat.format(track.trackTime)
        trackTimeTextView.text = formattedTime

        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_placeholder) // Плейсхолдер
            .centerInside()
            .transform(RoundedCorners(10))
            .into(artworkImageView)
    }
}
