// en network/RetrofitClient.kt
package com.example.mundial.network

import android.content.Context // <-- Importante
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://backappmundial.onrender.com/"

    // Interceptor de Logueo (ya lo tienes)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // --- Cliente PÚBLICO (para Login/Register) ---
    // Este cliente NO tiene el interceptor de Auth
    private val basicClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Instancia para AUTH API (Login, Register)
     * No necesita token.
     */
    val authInstance: AuthApi by lazy { // <-- Le cambié el nombre de 'instance' a 'authInstance'
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(basicClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    // --- Cliente PRIVADO (para el resto de la App) ---

    // Variable para guardar la instancia de ApiService (Singleton)
    @Volatile
    private var apiInstance: ApiService? = null

    /**
     * Crea u obtiene la instancia de ApiService (Autenticada)
     * Necesita el Context para leer SharedPreferences.
     */
    fun getApiService(context: Context): ApiService {
        // Usamos un 'synchronized' para asegurar que solo se cree una vez
        return apiInstance ?: synchronized(this) {
            apiInstance ?: buildApiService(context).also { apiInstance = it }
        }
    }

    private fun buildApiService(context: Context): ApiService {
        // 1. Creamos el interceptor de Auth (necesita el context)
        val authInterceptor = AuthInterceptor(context.applicationContext)

        // 2. Creamos el cliente de OkHttp AÑADIENDO el interceptor de Auth
        val authClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor) // <-- ¡AQUÍ ESTÁ LA MAGIA!
            .build()

        // 3. Creamos Retrofit con este nuevo cliente
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}