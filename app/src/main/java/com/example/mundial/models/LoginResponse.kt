package com.example.mundial.models

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)