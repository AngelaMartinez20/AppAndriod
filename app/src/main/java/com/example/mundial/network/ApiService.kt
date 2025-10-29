package com.example.mundial.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- Modelos de Datos ---

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val imagen: String?
)

data class CatalogoResponse(
    val message: String,
    val data: List<Producto>
)

data class CarritoResponse(
    val message: String,
    val subtotal: Double,
    val total: Double,
    val items: List<CarritoItem>
)

data class CarritoItem(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagen: String?,
    val totalItem: Double
)


// --- Interfaz de la API ---
interface ApiService {

    /**
     * Obtiene el catálogo de productos (Home)
     * GET /api/productos
     */
    @GET("api/productos")
    fun obtenerCatalogo(): Call<CatalogoResponse>

    /**
     * Obtiene el carrito del usuario (requiere token)
     * GET /api/carrito
     */
    @GET("api/carrito")
    fun obtenerCarrito(): Call<CarritoResponse>

    /** * ¡AQUÍ ESTABA EL ERROR DE COMENTARIO!
     * Agrega un producto al carrito (requiere token)
     * POST /api/carrito
     *
     * @param request Un Map que contiene "producto_id" y "cantidad"
     * @return Una respuesta simple (ej. {"message": "Producto agregado..."})
     */
    @POST("api/carrito")
    fun agregarAlCarrito(@Body request: Map<String, Int>): Call<Map<String, String>>
}

// (La llave '}' extra del final se eliminó)