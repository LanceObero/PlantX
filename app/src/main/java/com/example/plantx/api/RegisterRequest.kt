package com.example.plantx.api

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String,
    val confirm_password: String
)