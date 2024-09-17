package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val settingsBackButton = findViewById<Button>(R.id.settingsBack_button)

        settingsBackButton.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<ImageView>(R.id.share)

        shareButton.setOnClickListener {
            val shareMessage = getString(R.string.shareMessage)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        val supportButton = findViewById<ImageView>(R.id.support)

        supportButton.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.subject)
            val message = getString(R.string.message)

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
            }
                startActivity(intent)
        }
        val termsButton = findViewById<ImageView>(R.id.forward)

        termsButton.setOnClickListener {
            val url = getString(R.string.url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}