package com.example.mundial.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mundial.databinding.ActivityLoginBinding
import com.example.mundial.models.LoginRequest
import com.example.mundial.models.LoginResponse
import com.example.mundial.network.RetrofitClient
import com.example.mundial.ui.AdminActivity // <-- Asegúrate de importar AdminActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        // Usamos 'authInstance' (el cliente público)
        RetrofitClient.authInstance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "Response code: ${response.code()}, body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    val user = response.body()!!.user
                    val userRole = user.role // <-- Obtenemos el rol del usuario

                    // Guardamos el token y el rol
                    val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("jwt_token", token)
                        .putString("user_role", userRole) // <-- Guardamos el rol (buena práctica)
                        .apply()

                    Toast.makeText(applicationContext, "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()

                    // ---
                    // --- LÓGICA DE REDIRECCIÓN POR ROL ---
                    // ---
                    val intent: Intent
                    when (userRole) {
                        "admin" -> {
                            // Si es 'admin', va a AdminActivity
                            intent = Intent(this@LoginActivity, AdminActivity::class.java)
                        }
                        "usuario" -> {
                            // Si es 'usuario', va a MainActivity
                            intent = Intent(this@LoginActivity, MainActivity::class.java)
                        }
                        else -> {
                            // Si es cualquier otra cosa (ej. 'cajero'), va a MainActivity por defecto
                            Log.w("LoginActivity", "Rol '$userRole' no reconocido, enviando a MainActivity.")
                            intent = Intent(this@LoginActivity, MainActivity::class.java)
                        }
                    }

                    startActivity(intent)
                    finish() // Cerramos LoginActivity

                } else {
                    // Manejo de error mejorado (como lo vimos antes)
                    var errorMessage = "Credenciales inválidas"
                    try {
                        if (response.errorBody() != null) {
                            val errorJsonString = response.errorBody()!!.string()
                            if (errorJsonString.contains("message")) {
                                errorMessage = errorJsonString.split("\"")[3]
                            }
                        }
                    } catch (e: Exception) { /* Usar mensaje por defecto */ }
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error en login", t)
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}