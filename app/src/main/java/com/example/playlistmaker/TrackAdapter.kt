package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val trackList: MutableList<Track> = mutableListOf()) : RecyclerView.Adapter<TrackViewHolder>() {

    private var onTrackClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClickListener?.invoke(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    // Функция для обновления списка треков
    fun updateTracks(newTracks: List<Track>) {
        trackList.clear()
        trackList.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun setOnTrackClickListener(listener: (Track) -> Unit) {
        onTrackClickListener = listener
    }

    // Функция для очистки списка треков
    fun clearTracks() {
        trackList.clear()
        notifyDataSetChanged()
    }
}

