package com.example.amigosecretoguest

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

    //config retrofit
    private val retrofit =
        Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()

    //Chama a interface no mesmo tipo da classe requerida pela api
    private val create = retrofit.create(request::class.java)

    //Config Adapter
    val listadeJogos: MutableList<Sessao> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_management_session, container, false)

        val recyclerview = view.findViewById<RecyclerView>(R.id.Recycle)
        val sessoesButton = view.findViewById<Button>(R.id.versessoesbutton)
        val cpf = view.findViewById<EditText>(R.id.editcpf)
        val status = view.findViewById<TextView>(R.id.status)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)

        recyclerview.adapter = AdapterGame(requireContext(), listadeJogos)


        try {

            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            cpf.setText(sharedPref!!.getString("cpf", ""))

        } catch (_: Exception) {
        }


        sessoesButton.setOnClickListener {
            it.isClickable = false
            if (MainActivity().verify(cpf.text.toString(), 11, 14)) {
                status.text = getString(R.string.cpfinvalid_Text)
                it.isClickable = true
                return@setOnClickListener
            }

            try {
                create.getsessao2(cpf.text.toString()).apply {
                    enqueue(object : Callback<GetSessoes> {
                        override fun onResponse(
                                call: Call<GetSessoes>,
                                response: Response<GetSessoes>
                        ) {
                            //comparação de resposta
                            when (response.body()?.response) {
                                "200" -> { //Tudo certo
                                    status.text = ""
                                    for (i in 0 until response.body()!!.sessoes.size) {
                                        listadeJogos.add(
                                            Sessao(
                                                response.body()!!.sessoes[i],
                                                response.body()!!.tamanho[i]
                                            )
                                        )
                                    }
                                    it.isVisible = false
                                }

                                "001" -> { //CPF errado
                                    status.text = getString(R.string.cpfinvalid_Text)
                                    it.isClickable = true
                                }

                                "002" -> {  //Não é host
                                    status.text = getString(R.string.naoHost)
                                    it.isClickable = true
                                }
                            }
                        }

                        override fun onFailure(call: Call<GetSessoes>, t: Throwable) {
                            it.isClickable = true
                            status.text = getString(R.string.servidoresIndisponiveis)
                        }
                    })
                }


            } catch (e: Exception) {
                status.text = getString(R.string.aconteceuAlgumErro)
            }


        }

        return view
    }

}