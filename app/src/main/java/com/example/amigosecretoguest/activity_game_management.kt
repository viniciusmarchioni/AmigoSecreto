package com.example.amigosecretoguest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amigosecretoguest.adapter.AdapterGame
import com.example.amigosecretoguest.model.GetSessoes
import com.example.amigosecretoguest.model.Sessao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class activity_game_management() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_management)

        val recyclerview = findViewById<RecyclerView>(R.id.Recycle)
        val sessoesbut = findViewById<Button>(R.id.versessoesbutton)
        val cpf = findViewById<EditText>(R.id.editcpf)
        val status = findViewById<TextView>(R.id.status)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)


        //Config Adapter
        val listadeJogos: MutableList<Sessao> = mutableListOf()
        recyclerview.adapter = AdapterGame(this, listadeJogos)

        //config retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Chama a interface no mesmo tipo da classe requerida pela api
        val create = retrofit.create(request::class.java)



        sessoesbut.setOnClickListener {
            it.isClickable = false
            if (MainActivity().verify(cpf.text.toString(), 11, 14)) {
                status.text = "CPF inválido!"
                it.isClickable = true
                return@setOnClickListener
            }

            val call: Call<GetSessoes> =
                create.pegarSessao(GetSessoes(cpf.text.toString()))

            call.enqueue(object : Callback<GetSessoes> {

                override fun onResponse(call: Call<GetSessoes>, response: Response<GetSessoes>) {
                    //comparação de resposta
                    when (response.body()!!.response) {
                        "200" -> { //Tudo certo
                            status.text = ""
                            for (i in response.body()!!.sessoes) {
                                listadeJogos.add(Sessao(i))
                            }
                            it.isVisible = false
                        }

                        "001" -> { //CPF errado
                            status.text = "Servidor:\nCPF inválido!"
                            it.isClickable = true
                        }

                        "002" -> {  //Não é host
                            status.text = "Você não é um Host"
                            it.isClickable = true
                        }
                    }
                }

                override fun onFailure(call: Call<GetSessoes>, t: Throwable) {
                    it.isClickable = true
                    status.text = "Erro:\n$t"
                }


            })
        }
    }
}