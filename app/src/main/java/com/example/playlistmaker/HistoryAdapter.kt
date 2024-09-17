package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: MutableList<Track> = mutableListOf()) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(historyList[position])
        // Отключаем кликабельность для истории
        holder.itemView.setOnClickListener(null)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    // Функция для обновления списка истории
    fun updateHistory(newHistory: List<Track>) {
        historyList.clear()
        historyList.addAll(newHistory)
        notifyDataSetChanged()
    }

    // Функция для очистки истории
    fun clearHistory() {
        historyList.clear()
        notifyDataSetChanged()
    }
}
