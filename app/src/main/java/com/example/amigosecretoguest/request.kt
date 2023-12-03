package com.example.amigosecretoguest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface request {
    @POST("games/addguest/")
    fun addGuest(@Body dataclasse: User) : Call<User>



    @POST("games/create/")
    fun createGame(@Body dataclasse: User) : Call<User>


    @POST("games/sortition/verify")
    fun obterSorteio(@Body dataclasse: User) : Call<User>

}