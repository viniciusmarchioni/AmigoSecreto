package com.example.amigosecretoguest

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment(Home())

        val registrar = findViewById<ImageButton>(R.id.cadastrar)
        val criar = findViewById<ImageButton>(R.id.criar)
        val gerenciar = findViewById<ImageButton>(R.id.gerenciar)

        registrar.isClickable = false


        registrar.setOnClickListener {
            it.isClickable = false
            criar.isClickable = true
            gerenciar.isClickable = true
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.entry_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.frame, Home())
                .commit()
        }
        criar.setOnClickListener {
            it.isClickable = false
            registrar.isClickable = true
            gerenciar.isClickable = true
            if (supportFragmentManager.findFragmentById(R.id.frame) is Home) {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.entry_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frame, CreateSession())
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.entry_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.frame, CreateSession())
                    .commit()
            }
        }

        gerenciar.setOnClickListener {
            it.isClickable = false
            registrar.isClickable = true
            criar.isClickable = true
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.entry_right_to_left, R.anim.exit_right_to_left)
                .replace(R.id.frame, ManagementSession())
                .commit()
        }

    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    fun verify(editText: String, min: Int, max: Int): Boolean {
        if (editText.length < min || editText.length > max) {
            return true
        }
        return false
    }

}