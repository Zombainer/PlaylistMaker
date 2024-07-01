package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val settingsBackButton = findViewById<Button>(R.id.settingsBack_button)

        settingsBackButton.setOnClickListener {
            finish()
        }
    }
}