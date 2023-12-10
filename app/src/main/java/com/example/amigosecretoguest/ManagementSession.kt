package com.example.amigosecretoguest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ManagementSession : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_management_session, container, false)

        val recyclerview = view.findViewById<RecyclerView>(R.id.Recycle)
        val sessoesButton = view.findViewById<Button>(R.id.versessoesbutton)
        val cpf = view.findViewById<EditText>(R.id.editcpf)
        val status = view.findViewById<TextView>(R.id.status)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)

        //Config Adapter
        val listadeJogos: MutableList<Sessao> = mutableListOf()
        recyclerview.adapter = AdapterGame(requireContext(), listadeJogos)

        //config retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Chama a interface no mesmo tipo da classe requerida pela api
        val create = retrofit.create(request::class.java)

        sessoesButton.setOnClickListener {
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

        return view
    }

}