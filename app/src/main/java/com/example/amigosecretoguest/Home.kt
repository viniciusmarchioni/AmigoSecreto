package com.example.amigosecretoguest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.amigosecretoguest.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home : Fragment() {

    //Configura o retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Chama a interface no mesmo tipo da classe requerida pela api
    private val create: request = retrofit.create(request::class.java)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //infla o layout para possibilitar o findViewById
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cadastrar = view.findViewById<Button>(R.id.cadastrar)
        val verificar = view.findViewById<Button>(R.id.verificar)
        val status = view.findViewById<TextView>(R.id.status)
        val editCpf = view.findViewById<EditText>(R.id.cpf_edit)
        val editName = view.findViewById<EditText>(R.id.nome_edit)
        val editDesejo = view.findViewById<EditText>(R.id.desejo_edit)
        val editId = view.findViewById<EditText>(R.id.gameId)

        try {
            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            editCpf.setText(sharedPref!!.getString("cpf",""))
            editId.setText(sharedPref.getString("session",""))
            editName.setText(sharedPref.getString("nome",""))


        }catch (_:Exception){}



        cadastrar.setOnClickListener {
            it.isClickable = false
            if (MainActivity().verify(editCpf.text.toString(), 11, 14)) {
                status.text = "CPF inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (MainActivity().verify(editName.text.toString(), 2, 20)) {
                status.text = "Nome inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (MainActivity().verify(editDesejo.text.toString(), 5, 255)) {
                status.text = "Desejo inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (MainActivity().verify(editId.text.toString(), 10, 10)) {
                status.text = "ID de jogo não encontrado."
                it.isClickable = true
                return@setOnClickListener
            }



            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val editor = sharedPref!!.edit()
            editor.putString("cpf",editCpf.text.toString())
            editor.putString("session",editId.text.toString())
            editor.putString("nome",editName.text.toString())
            editor.apply()



            try {
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
                        //#001-tbid/002-cpf/003-nome/004-desejo/005-cad/
                        //volta para primeiro plano

                        if ("200" in response.body()!!.response) {
                            status.text = "Cadastro realizado!"
                            it.isClickable = false
                            it.isVisible = false
                            return
                        }


                        when (response.body()!!.response) {

                            "001" -> status.text = "Sessão não encontrada."
                            "002" -> status.text = "Servidor:\nCPF inválido."
                            "003" -> status.text = "Servidor:\nNome inválido."
                            "004" -> status.text = "Servidor:\nDesejo inválido."
                            "005" -> status.text = "Você já está cadastrado."
                            "100" -> status.text = "Aconteceu algum erro."

                        }
                        it.isClickable = true
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        status.text = "Servidores indisponíveis\nTente novamente mais tarde."
                        it.isClickable = true
                    }

                })


            } catch (e: Exception) {
                status.text = "Aconteceu algum erro durante o cadastro."
            }

        }

        verificar.setOnClickListener {
            it.isClickable = false
            if (MainActivity().verify(editCpf.text.toString(), 11, 14)) {
                status.text = "CPF inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (MainActivity().verify(editId.text.toString(), 10, 10)) {
                status.text = "Sessão não encontrada."
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
                    //   #001-cpf/002-tableid/003-ncad/004-naconteceu
                    if ("200" in response.body()!!.response) {
                        status.text =
                            "Você tirou o(a) ${response.body()!!.nome}\n" +
                                    "e ele(a) deseja ${response.body()!!.desejo}"
                        it.isClickable = false
                        it.isVisible = false
                        return
                    }


                    when (response.body()!!.response) {

                        "001" -> status.text = "Servidor: CPF inválido."
                        "002" -> status.text = "Servidor: Sessão não encontrada"
                        "003" -> status.text = "Você não está cadastrado na sessão."
                        "004" -> status.text = "O sorteio ainda não aconteceu."

                    }
                    it.isClickable = true
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<User>, t: Throwable) {
                    it.isClickable = true
                    status.text = "Ocorreu um erro:\n$t"
                }
            })


        }

        return view
    }
}