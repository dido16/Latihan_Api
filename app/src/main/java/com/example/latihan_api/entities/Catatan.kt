package com.example.latihan_api.entities

data class Catatan(
    val id: Int? = null,
    val judul: String,
    val isi: String,
    val user_id: Int
)
