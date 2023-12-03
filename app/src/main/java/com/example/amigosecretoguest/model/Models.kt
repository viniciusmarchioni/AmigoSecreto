package com.example.amigosecretoguest.model

data class User (

    val tableID: String,
    val nome: String,
    val cpf: String,
    val desejo: String,
    val response: String = ""

)

data class Sessoes (

    val cpf: String,
    val sessoes: MutableList<String> = mutableListOf(),
    val response: String = ""

)

data class Game (
    val gameID: String
)