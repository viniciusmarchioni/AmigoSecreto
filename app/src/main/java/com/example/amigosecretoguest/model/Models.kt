package com.example.amigosecretoguest.model

data class User (

    val tableID: String,
    val nome: String,
    val cpf: String,
    val desejo: String,
    val response: String = ""

)
data class GetSessoes (

    val sessoes: MutableList<String> = mutableListOf(),
    val tamanho: MutableList<Int> = mutableListOf(),
    val response: String = ""

)

data class Sessao (
    val sessionID: String,
    val size: Int
)

data class Sorteio (
    val decisao: Boolean,
    val sessaoid: String,
    val response: Boolean = false
)

data class Apagar(val response: String)
