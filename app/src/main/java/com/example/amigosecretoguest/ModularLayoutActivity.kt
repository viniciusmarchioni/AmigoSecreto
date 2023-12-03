package com.example.amigosecretoguest

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ModularLayoutActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modular_layout)
        val text = findViewById<TextView>(R.id.texto)

        text.text = intent.getStringExtra("titulo")

    }
}