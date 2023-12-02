package com.example.amigosecretoguest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface request {
    @POST("games/addguest/")
    fun addGuest(@Body dataclasse: User) : Call<User>



    @POST("games/create/")
    fun createGame(@Body dataclasse: User) : Call<User>

}