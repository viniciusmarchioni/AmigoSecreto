package com.example.amigosecretoguest

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Connection
import java.sql.DriverManager
import java.util.Random


class CreateGameActivity : AppCompatActivity() {

    private lateinit var id: String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        val cpfedit = findViewById<EditText>(R.id.cpf_edit)
        val name = findViewById<EditText>(R.id.nome_edit)
        val desejo = findViewById<EditText>(R.id.desejo_edit)
        val createButton = findViewById<Button>(R.id.criar)
        val status = findViewById<TextView>(R.id.status)
        val copy = findViewById<ImageButton>(R.id.copy)
        val join = findViewById<Button>(R.id.entrar)


        createButton.setOnClickListener {
            it.isClickable = false
            if (!MainActivity().verify(cpfedit.text.toString(), 11, 14)) {
                status.text = "CPF inválido"
                it.isClickable = true
                return@setOnClickListener
            } else if (!MainActivity().verify(name.text.toString(), 2, 20)) {
                status.text = "Nome inválido"
                it.isClickable = true
                return@setOnClickListener
            } else if (!MainActivity().verify(desejo.text.toString(), 5, 255)) {
                status.text = "Desejo inválido"
                it.isClickable = true
                return@setOnClickListener
            }

            val obj =
                User("", name.text.toString(), cpfedit.text.toString(), desejo.text.toString())


            //config retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            //Chama a interface no mesmo tipo da classe requerida pela api
            val create = retrofit.create(request::class.java)


            //chama função da interface
            val call: Call<User> = create.createGame(obj)


            //roda em segundo plano
            call.enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    //volta para primeiro plano
                    Handler(Looper.getMainLooper()).post {

                        // Exemplo de alteração na UI
                        status.text = response.body()?.response

                        if ("Jogo criado" !in response.body()!!.response) {
                            it.isClickable = true
                            return@post
                        }
                        id = response.body()!!.tableID
                        copy.isVisible = true
                        copy.isClickable = true
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


        copy.setOnClickListener {
            val clipboardManager: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label",id)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Copiado!", Toast.LENGTH_SHORT).show();
        }

        join.setOnClickListener {
            val novaTela = Intent(this, MainActivity::class.java)
            startActivity(novaTela)
        }
    }

}