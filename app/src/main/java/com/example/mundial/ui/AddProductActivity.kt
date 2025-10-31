package com.example.mundial.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem // <-- (Ya lo tenías, ¡bien!)
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mundial.databinding.ActivityAddProductBinding
import com.example.mundial.network.ApiService
import com.example.mundial.network.CreateProductRequest
import com.example.mundial.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- ¡AQUÍ ESTÁ LA LÍNEA QUE FALTABA! ---
        // 1. Le dice a la app que tu Toolbar (con id 'toolbar') es la barra de acción
        setSupportActionBar(binding.toolbar)

        // 2. Ahora esto SÍ funciona, porque supportActionBar ya no es nulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // ------------------------------------

        // Obtener el servicio de API (autenticado)
        apiService = RetrofitClient.getApiService(applicationContext)

        // Configurar el click del botón
        binding.btnGuardarProducto.setOnClickListener {
            crearNuevoProducto()
        }
    }

    // (Esta función ya la tenías bien)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 'android.R.id.home' es el ID de la flecha de regreso
        if (item.itemId == android.R.id.home) {
            finish() // Cierra esta Activity y regresa a la anterior
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // (Esta función ya la tenías bien)
    private fun crearNuevoProducto() {
        // 1. Obtener datos del formulario
        val nombre = binding.etNombre.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val precio = binding.etPrecio.text.toString().toDoubleOrNull()
        val stock = binding.etStock.text.toString().toIntOrNull()
        val imagenUrl = binding.etImagenes.text.toString().trim()

        // 2. Validar
        if (nombre.isEmpty() || descripcion.isEmpty() || precio == null || stock == null || imagenUrl.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Crear el objeto de la petición
        val request = CreateProductRequest(
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            imagenes = listOf(imagenUrl)
        )

        // 4. Llamar a la API
        apiService.createProduct(request).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddProductActivity, "✅ Producto creado con éxito", Toast.LENGTH_LONG).show()
                    Log.d("AddProduct", "Producto creado: ${response.body()?.get("message")}")
                    finish() // Cierra el formulario y regresa al AdminActivity
                } else {
                    Log.e("AddProduct", "Error al crear producto: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@AddProductActivity, "Error ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("AddProduct", "Fallo de red al crear producto", t)
                Toast.makeText(this@AddProductActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}