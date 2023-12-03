package com.example.amigosecretoguest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.amigosecretoguest.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cadButton = findViewById<Button>(R.id.cadastrar)
        val verButton = findViewById<Button>(R.id.verificar)
        val troca = findViewById<Button>(R.id.troca)
        val status = findViewById<TextView>(R.id.status)
        val editCpf = findViewById<EditText>(R.id.cpf_edit)
        val editName = findViewById<EditText>(R.id.nome_edit)
        val editDesejo = findViewById<EditText>(R.id.desejo_edit)
        val editId = findViewById<EditText>(R.id.gameId)

        //config retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Chama a interface no mesmo tipo da classe requerida pela api
        val create = retrofit.create(request::class.java)



        cadButton.setOnClickListener {

            it.isClickable = false
            if (verify(editCpf.text.toString(), 11, 14)) {
                status.text = "CPF inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (verify(editName.text.toString(), 2, 20)) {
                status.text = "Nome inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (verify(editDesejo.text.toString(), 5, 255)) {
                status.text = "Desejo inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (verify(editId.text.toString(), 10, 10)) {
                status.text = "ID de jogo não encontrado."
                it.isClickable = true
                return@setOnClickListener
            }


            //chama função da interface
            val call: Call<User> = create.addGuest(
                User(
                    editId.text.toString(),
                    editName.text.toString(),
                    editCpf.text.toString(),
                    editDesejo.text.toString()
                )
            )


            //roda em segundo plano
            call.enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    //volta para primeiro plano
                    Handler(Looper.getMainLooper()).post {

                        if ("200" !in response.body()!!.response) {

                            status.text = response.body()?.response
                            it.isClickable = true
                            return@post

                        }
                        it.isVisible = false
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<User>, t: Throwable) {
                    status.text = "Erro!\n$t"
                    Handler(Looper.getMainLooper()).post {
                        it.isClickable = true
                    }
                }

            })


        }

        verButton.setOnClickListener {
            it.isClickable = false
            if (!verify(editCpf.text.toString(), 11, 14)) {
                status.text = "CPF inválido."
                it.isClickable = true
                return@setOnClickListener
            }

            //chama função da interface
            val call: Call<User> =
                create.obterSorteio(
                    User(
                        editId.text.toString(),
                        editName.text.toString(),
                        editCpf.text.toString(),
                        editDesejo.text.toString()
                    )
                )

            call.enqueue(object : Callback<User> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<User>, response: Response<User>) {


                    if("200" !in response.body()!!.response){
                        status.text = response.body()!!.response
                        return
                    }
                    it.isVisible = false
                    status.text =
                        "Você tirou o(a)${response.body()!!.nome}\nE ele(a) deseja ${response.body()!!.desejo}"
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<User>, t: Throwable) {
                    it.isClickable = true
                    status.text = "Ocorreu um erro:\n$t"
                }
            })


        }

        troca.setOnClickListener {
            startActivity(Intent(this, CreateGameActivity::class.java))
        }

    }


    fun verify(editText: String, min: Int, max: Int): Boolean {
        if (editText.length < min || editText.length > max) {
            return true
        }
        return false
    }

}
