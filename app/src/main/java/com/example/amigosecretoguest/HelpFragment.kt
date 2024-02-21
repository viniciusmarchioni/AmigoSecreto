package com.example.amigosecretoguest

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class HelpFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_help, container, false)

        val sobre = view.findViewById<TextView>(R.id.sobre)

        sobre.setOnClickListener {
            startActivity(Intent(requireActivity(),sobreActivity::class.java))
        }



        return view
    }

}