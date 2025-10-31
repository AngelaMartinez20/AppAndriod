package com.example.mundial.ui

// --- Imports ---
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mundial.databinding.ActivityMainBinding
import com.example.mundial.network.ApiService
import com.example.mundial.network.CarritoResponse
import com.example.mundial.network.RetrofitClient
import com.example.mundial.ui.CatalogoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService
    private lateinit var carritoAdapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activa la nueva Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        // 1. Revisar el token
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 2. Si el token SÍ existe, continuar
        setupRecyclerView() // <-- Esta es la llamada
        apiService = RetrofitClient.getApiService(applicationContext)
        obtenerMiCarrito()

        // Configurar el botón de logout
        binding.btnLogout.setOnClickListener {
            prefs.edit().remove("jwt_token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Configurar el botón de ir al Catálogo
        binding.fabAgregarProductos.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }

        // Lógica del botón Comprar
        binding.btnComprar.setOnClickListener {
            Toast.makeText(this, "Iniciando proceso de pago...", Toast.LENGTH_SHORT).show()
        }
    }
    // --- AQUÍ TERMINA EL onCREATE ---


    /**
     * ¡ESTA ES LA FUNCIÓN QUE FALTABA!
     * Función para configurar el RecyclerView y el Adapter
     */
    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(emptyList())
        binding.rvCarrito.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = carritoAdapter
        }
    }


    /**
     * Función para obtener los datos del carrito desde la API.
     */
    private fun obtenerMiCarrito() {
        apiService.obtenerCarrito().enqueue(object : Callback<CarritoResponse> {

            override fun onResponse(call: Call<CarritoResponse>, response: Response<CarritoResponse>) {
                if (response.isSuccessful) {
                    val carrito = response.body()
                    Log.d("MainActivity", "Carrito obtenido: ${carrito?.items?.size} items")

                    if (carrito != null && carrito.items.isNotEmpty()) {
                        carritoAdapter.updateItems(carrito.items)
                        binding.tvTotal.text = "Total: $${carrito.total}"
                        binding.bottomBar.visibility = View.VISIBLE

                    } else {
                        carritoAdapter.updateItems(emptyList())
                        binding.bottomBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // Manejar errores
                    Log.e("MainActivity", "Error ${response.code()}: ${response.message()}")
                    if (response.code() == 401 || response.code() == 403) {
                        Toast.makeText(this@MainActivity, "Tu sesión ha expirado", Toast.LENGTH_SHORT).show()
                        binding.btnLogout.performClick()
                    } else {
                        Toast.makeText(this@MainActivity, "Error al obtener carrito: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<CarritoResponse>, t: Throwable) {
                Log.e("MainActivity", "Fallo de red", t)
                Toast.makeText(this@MainActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}