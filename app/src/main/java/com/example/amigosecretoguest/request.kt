package com.example.amigosecretoguest

import com.example.amigosecretoguest.model.GetSessoes
import com.example.amigosecretoguest.model.User
import com.example.amigosecretoguest.model.Apagar
import com.example.amigosecretoguest.model.Sorteio
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface request {
    @POST("/session/add/")
    fun addGuest(@Body dataclasse: User): Call<User>


    @POST("/session/create/")
    fun createGame(@Body dataclasse: User): Call<User>


    @POST("/session/sortition/verify")
    fun obterSorteio(@Body dataclasse: User): Call<User>


    @DELETE("/session/delete/{id}")
    fun apagarsessao(@Path("id") id: String): Call<Apagar>

    @POST("/session/sortition/start")
    fun realizarsorteio(@Body dataclasse: Sorteio): Call<Sorteio>

    @GET("/session/requere/{cpf}")
    fun getsessao2(@Path("cpf") cpf: String): Call<GetSessoes>
}