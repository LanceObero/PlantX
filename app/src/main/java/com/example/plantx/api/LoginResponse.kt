package com.example.plantx.api

data class LoginResponse(
    val refresh: String,
    val access: String,
    val email: String,
    val full_name: String
)