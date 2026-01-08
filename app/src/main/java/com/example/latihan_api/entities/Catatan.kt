package com.example.latihan_api.entities

data class Catatan(
    val id: Int?,
    val judul: String,
    val isi: String,
    val user_id: Int? = 1
)