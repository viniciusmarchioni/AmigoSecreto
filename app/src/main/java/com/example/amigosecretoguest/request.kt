package com.example.amigosecretoguest

import com.example.amigosecretoguest.model.GetSessoes
import com.example.amigosecretoguest.model.GetSessoes2
import com.example.amigosecretoguest.model.User
import com.example.amigosecretoguest.model.apagar
import com.example.amigosecretoguest.model.apagar2
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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


    @DELETE("/session/delete/{id}")
    fun apagarsessao2(@Path("id") id: String): Call<apagar2>

    @POST("/session/sortition/start")
    fun realizarsorteio(@Body dataclasse: apagar): Call<apagar>

    @GET("/session/requere/{cpf}")
    fun getsessao2(@Path("cpf") cpf: String): Call<GetSessoes2>
}