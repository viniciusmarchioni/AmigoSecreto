package com.example.amigosecretoguest

import com.example.amigosecretoguest.model.GetSessoes
import com.example.amigosecretoguest.model.User
import com.example.amigosecretoguest.model.apagar
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface request {
    @POST("/session/add/")
    fun addGuest(@Body dataclasse: User): Call<User>


    @POST("/session/create/")
    fun createGame(@Body dataclasse: User): Call<User>


    @POST("/session/sortition/verify")
    fun obterSorteio(@Body dataclasse: User): Call<User>


    @POST("/session/requere")
    fun pegarSessao(@Body dataclasse: GetSessoes): Call<GetSessoes>


    @POST("/session/delete")
    fun apagarsessao(@Body dataclasse: apagar): Call<apagar>

    @POST("/session/sortition/start")
    fun realizarsorteio(@Body dataclasse: apagar): Call<apagar>


}