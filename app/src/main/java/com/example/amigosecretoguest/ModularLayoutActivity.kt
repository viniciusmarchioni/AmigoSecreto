package com.example.amigosecretoguest

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.amigosecretoguest.model.Sorteio
import com.example.amigosecretoguest.model.Apagar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ModularLayoutActivity() : AppCompatActivity() {

    //config retrofit
    val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    //Chama a interface no mesmo tipo da classe requerida pela api
    val create = retrofit.create(request::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modular_layout)
        val text = findViewById<TextView>(R.id.texto)
        text.text = intent.getStringExtra("titulo")
        val delete = findViewById<Button>(R.id.deletar)
        val sorteio = findViewById<Button>(R.id.sortear)
        val status = findViewById<TextView>(R.id.status)

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
                val call: Call<Apagar> = create.apagarsessao(text.text.toString())

                call.enqueue(object : Callback<Apagar> {
                    override fun onResponse(call: Call<Apagar>, response: Response<Apagar>) {
                        if (response.body()!!.response == "200") {
                            startActivity(
                                    Intent(
                                            this@ModularLayoutActivity,
                                            MainActivity::class.java
                                    )
                            )
                        }
                    }

                    override fun onFailure(call: Call<Apagar>, t: Throwable) {
                        status.text = getString(R.string.aconteceuAlgumErro)
                    }


                })


            }
        }


        sorteio.setOnClickListener {
            it.isClickable = false

            //chama função da interface
            val call: Call<Sorteio> =
                    create.realizarsorteio(
                            Sorteio(true, text.text.toString())
                    )

            call.enqueue(object : Callback<Sorteio> {

                override fun onResponse(call: Call<Sorteio>, response: Response<Sorteio>) {
                    if (response.body()!!.response) {
                        status.text = getString(R.string.sorteioRealizado)
                        return
                    } else {
                        status.text = getString(R.string.sorteioAlerta)
                    }
                }

                override fun onFailure(call: Call<Sorteio>, t: Throwable) {
                    status.text = "Erro:\n$t"
                }
            })


        }


    }
}