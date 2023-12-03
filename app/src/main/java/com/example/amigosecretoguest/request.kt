package com.example.amigosecretoguest

import com.example.amigosecretoguest.model.Sessoes
import com.example.amigosecretoguest.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface request {
    @POST("games/addguest/")
    fun addGuest(@Body dataclasse: User): Call<User>


    @POST("games/create/")
    fun createGame(@Body dataclasse: User): Call<User>


    @POST("games/sortition/verify")
    fun obterSorteio(@Body dataclasse: User): Call<User>


    @POST("/games/requere")
    fun pegarSessao(@Body dataclasse: Sessoes): Call<Sessoes>

}