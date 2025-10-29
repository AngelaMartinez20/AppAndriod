package com.example.mundial.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    // Obtenemos una referencia a las SharedPreferences
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Obtenemos la petición original
        val originalRequest = chain.request()

        // 2. Leemos el token de SharedPreferences
        val token = sharedPrefs.getString("jwt_token", null)

        // 3. Si no hay token (ej. en Login), dejamos pasar la petición original
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // 4. Si SÍ hay token, construimos una nueva petición con el Header
        val newRequest = originalRequest.newBuilder()
            // ¡Importante el "Bearer "! Es el estándar de JWT
            .addHeader("Authorization", "Bearer $token")
            .build()

        // 5. Dejamos pasar la nueva petición (ya con el token)
        return chain.proceed(newRequest)
    }
}