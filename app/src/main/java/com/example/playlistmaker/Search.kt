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

class Search : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editText = findViewById(R.id.searchEditText)

        val searchBackButton = findViewById<Button>(R.id.searchBack_button)
        val editText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearSearchButton)

        searchBackButton.setOnClickListener {
            finish()
        }

        val searchIcon: Drawable? = getDrawable(R.drawable.search_icon)

        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(searchIcon, null, null, null)
        editText.compoundDrawablePadding = 16

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                searchText = editable?.toString() ?: ""
                if (editable.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }
        })

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearButton.visibility = if (editText.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            } else {
                clearButton.visibility = View.INVISIBLE
            }
        }

        clearButton.setOnClickListener {
            editText.text.clear()
            clearButton.visibility = View.INVISIBLE
            hideKeyboard()
        }

        // Восстановление сохраненных данных из savedInstanceState, если они есть
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
            editText.setText(searchText)
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchText", searchText) // Сохранение текста из EditText в Bundle
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "") // Восстановление данных из Bundle
    }
}
