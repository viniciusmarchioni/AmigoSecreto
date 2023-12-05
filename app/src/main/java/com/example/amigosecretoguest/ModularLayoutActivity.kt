package com.example.amigosecretoguest

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.amigosecretoguest.model.User
import com.example.amigosecretoguest.model.apagar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ModularLayoutActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modular_layout)
        val text = findViewById<TextView>(R.id.texto)
        text.text = intent.getStringExtra("titulo")
        val delete = findViewById<Button>(R.id.deletar)
        val sorteio = findViewById<Button>(R.id.sortear)
        val status = findViewById<TextView>(R.id.status)


        //config retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Chama a interface no mesmo tipo da classe requerida pela api
        val create = retrofit.create(request::class.java)



        delete.setOnClickListener {
            val deleteLayout = findViewById<RelativeLayout>(R.id.layoutdelete)
            deleteLayout.isVisible = true
            deleteLayout.isClickable = true

            val sim = findViewById<Button>(R.id.simButton)
            val nao = findViewById<Button>(R.id.naoButton)

            deleteLayout.setOnClickListener {
                it.isVisible = false
                it.isClickable = false
            }

            nao.setOnClickListener {
                deleteLayout.isVisible = false
                deleteLayout.isClickable = false
            }

            sim.setOnClickListener {

                val call: Call<apagar> = create.apagarsessao(
                    apagar(true, text.text.toString())
                )

                call.enqueue(object : Callback<apagar> {

                    override fun onResponse(call: Call<apagar>, response: Response<apagar>) {
                        if (response.body()!!.response) {

                            startActivity(
                                Intent(
                                    this@ModularLayoutActivity,
                                    CreateGameActivity::class.java
                                )
                            )

                        }
                    }

                    override fun onFailure(call: Call<apagar>, t: Throwable) {

                    }


                })



                Log.d("TAG", "APAGA")
            }

        }


        sorteio.setOnClickListener {
            it.isClickable = false

            //chama função da interface
            val call: Call<apagar> =
                create.realizarsorteio(
                    apagar(true, text.text.toString())
                )

            call.enqueue(object : Callback<apagar> {

                override fun onResponse(call: Call<apagar>, response: Response<apagar>) {
                    if (response.body()!!.response) {
                        status.text = "Sorteio realizado!"
                        return
                    }
                    else {
                        status.text = "Só é possivel realizar o sorteio com pelo menos 3 participantes no jogo."
                    }
                }

                override fun onFailure(call: Call<apagar>, t: Throwable) {
                    status.text = "Erro:\n$t"
                }
            })


        }


    }
}