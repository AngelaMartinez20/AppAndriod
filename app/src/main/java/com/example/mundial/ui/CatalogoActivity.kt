package com.example.mundial.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem // <-- 1. ¡IMPORT FALTANTE!
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mundial.databinding.ActivityCatalogoBinding
import com.example.mundial.network.ApiService
import com.example.mundial.network.CatalogoResponse
import com.example.mundial.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.mundial.ui.CatalogoAdapter // <-- 2. ¡IMPORT CORREGIDO!

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var apiService: ApiService
    private lateinit var catalogoAdapter: CatalogoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- 3. ¡LÍNEA FALTANTE! ---
        // Esto le dice a Android que muestre la flecha de "regresar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtener el servicio de API (ya tiene el token)
        apiService = RetrofitClient.getApiService(applicationContext)

        // Configurar el RecyclerView y el Adapter
        setupRecyclerView()

        // Llamar a la API para obtener los productos
        obtenerCatalogoDeProductos()

        // Lógica para el botón flotante (¡Esta parte ya la tenías bien!)
        binding.fabVerCarrito.setOnClickListener {
            finish() // Cierra esta pantalla y regresa a MainActivity
        }
    }

    // Esta función maneja el clic de la flecha de regreso
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Cierra esta Activity y regresa a MainActivity
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        catalogoAdapter = CatalogoAdapter(emptyList(), ::handleAgregarClick)

        binding.rvCatalogo.apply {
            layoutManager = LinearLayoutManager(this@CatalogoActivity)
            adapter = catalogoAdapter
        }
    }

    private fun obtenerCatalogoDeProductos() {
        apiService.obtenerCatalogo().enqueue(object : Callback<CatalogoResponse> {
            override fun onResponse(call: Call<CatalogoResponse>, response: Response<CatalogoResponse>) {
                if (response.isSuccessful) {
                    val productos = response.body()?.data
                    if (productos != null && productos.isNotEmpty()) {
                        catalogoAdapter.updateProductos(productos)
                    } else {
                        Toast.makeText(this@CatalogoActivity, "No hay productos disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("CatalogoActivity", "Error al obtener catálogo: ${response.code()}")
                    Toast.makeText(this@CatalogoActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CatalogoResponse>, t: Throwable) {
                Log.e("CatalogoActivity", "Fallo de red al obtener catálogo", t)
                Toast.makeText(this@CatalogoActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleAgregarClick(productoId: Int) {
        Log.d("CatalogoActivity", "Agregando producto ID: $productoId al carrito")
        val requestBody = mapOf("producto_id" to productoId, "cantidad" to 1)

        apiService.agregarAlCarrito(requestBody).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CatalogoActivity, "✅ Producto agregado", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("CatalogoActivity", "Error al agregar al carrito: ${response.code()}")
                    Toast.makeText(this@CatalogoActivity, "Error al agregar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("CatalogoActivity", "Fallo de red al agregar al carrito", t)
                Toast.makeText(this@CatalogoActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}