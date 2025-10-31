package com.example.mundial.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mundial.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // En AdminActivity.kt, dentro de onCreate
        binding.btnGoToUserList.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        binding.btnGoToAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        binding.btnLogoutAdmin.setOnClickListener {
            // 1. Borrar el token de SharedPreferences
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().remove("jwt_token").apply()

            // 2. Crear un intent para ir al Login
            val intent = Intent(this, LoginActivity::class.java)

            // 3. (IMPORTANTE) Limpiar el historial de pantallas
            // Esto evita que el usuario pueda "regresar" al panel de admin
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish() // Cierra esta AdminActivi
        }
    }
}