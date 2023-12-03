package com.example.amigosecretoguest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.amigosecretoguest.R
import android.content.Context
import android.content.Intent
import com.example.amigosecretoguest.model.Game
import com.example.amigosecretoguest.ModularLayoutActivity

class AdapterGame(private val context: Context, private val games: MutableList<Game>) :
    RecyclerView.Adapter<AdapterGame.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val item =
            LayoutInflater.from(context).inflate(R.layout.holder_recycle_layout, parent, false)
        return GameViewHolder(item)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.buttonGameID.text = games[position].gameID
        holder.buttonGameID.setOnClickListener {
            val novaTela = Intent(context, ModularLayoutActivity::class.java)
            novaTela.putExtra("titulo", holder.buttonGameID.text)
            context.startActivity(novaTela)
        }
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttonGameID = itemView.findViewById<Button>(R.id.gameidbutton)
    }

}