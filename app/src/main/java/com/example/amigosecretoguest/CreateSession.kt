package com.example.amigosecretoguest

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.amigosecretoguest.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CreateSession : Fragment() {

    private lateinit var id: String

    //config retrofit
    val retrofit = Retrofit.Builder().baseUrl("http://18.230.152.190:5000/")
        .addConverterFactory(GsonConverterFactory.create()).build()


    //Chama a interface no mesmo tipo da classe requerida pela api
    val create = retrofit.create(request::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_session, container, false)

        val cpfedit = view.findViewById<EditText>(R.id.cpf_edit)
        val name = view.findViewById<EditText>(R.id.nome_edit)
        val desejo = view.findViewById<EditText>(R.id.desejo_edit)
        val createButton = view.findViewById<Button>(R.id.criar)
        val status = view.findViewById<TextView>(R.id.status)
        val copy = view.findViewById<ImageButton>(R.id.copy)

        //tenta atribuir o valor no campo de cpf, se falhar é pq não existe, então ele passa
        try {

            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            cpfedit.setText(sharedPref!!.getString("cpf",""))
            name.setText(sharedPref.getString("nome",""))
        }catch (_:Exception){}

        createButton.setOnClickListener {
            it.isClickable = false
            if (isValidText(cpfedit.text.toString(), 2, 14)) {
                status.text = "CPF inválido."
                it.isClickable = true
                return@setOnClickListener
            } else if (isValidText(name.text.toString(), 2, 20)) {
                status.text = "Nome inválido"
                it.isClickable = true
                return@setOnClickListener
            } else if (isValidText(desejo.text.toString(), 5, 255)) {
                status.text = "Desejo inválido"
                it.isClickable = true
                return@setOnClickListener
            }


            //chama função da interface
            create.createGame(
                User(
                    "", name.text.toString(), cpfedit.text.toString(), desejo.text.toString()
                )
            ).apply {
                enqueue(object : Callback<User> {

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        val sharedPref = activity?.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        val editor = sharedPref!!.edit()
                        editor.putString("cpf",cpfedit.text.toString())
                        editor.apply()

                        //Neste caso é necessário para inicializar o ID
                        Handler(Looper.getMainLooper()).post {

                            if ("200" !in response.body()!!.response) {
                                status.text = response.body()?.response
                                it.isClickable = true
                                return@post
                            }
                            id = response.body()!!.tableID
                            status.text = "Jogo Criado!\nO ID da sua sessão é:\n$id"
                            copy.isVisible = true
                            copy.isClickable = true
                            it.isVisible = false
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        status.text = "Falha na conexão com o servidor."
                        it.isClickable = true
                    }

                })

            }
        }

        copy.setOnClickListener {
            val clipboardManager: ClipboardManager =
                requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", id)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "Copiado!", Toast.LENGTH_SHORT).show()
        }


        return view
    }


    companion object {
        fun isValidText(text: String, minLength: Int, maxLength: Int): Boolean {
            if (text.length < minLength || text.length > maxLength) {
                return true
            }
            return false
        }
    }


}