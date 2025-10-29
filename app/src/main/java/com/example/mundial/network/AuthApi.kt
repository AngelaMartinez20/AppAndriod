package com.example.mundial.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.mundial.models.LoginRequest
import com.example.mundial.models.LoginResponse

interface AuthApi {
    @POST("auth/login") // ✅ ruta correcta
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register") // ✅ ruta correcta
    fun register(@Body request: Map<String, String>): Call<Map<String, String>>
}
