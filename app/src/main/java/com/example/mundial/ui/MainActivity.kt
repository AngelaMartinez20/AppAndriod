package com.example.mundial.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mundial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnLogout.setOnClickListener {
            prefs.edit().remove("jwt_token").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
