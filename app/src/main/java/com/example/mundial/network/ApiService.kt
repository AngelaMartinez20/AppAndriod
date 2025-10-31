package com.example.mundial.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE // <-- Faltaba
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT // <-- Faltaba
import retrofit2.http.Path

// --- Modelos de Datos ---

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val role: String
)

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

data class CreateProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenes: List<String>
)


// --- Interfaz de la API ---

interface ApiService {

    // --- RUTAS DE USUARIO (Públicas / Usuario normal) ---

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

    /**
     * Agrega un producto al carrito (requiere token)
     * POST /api/carrito
     *
     * @param request Un Map que contiene "producto_id" y "cantidad"
     * @return Una respuesta simple (ej. {"message": "Producto agregado..."})
     */
    @POST("api/carrito")
    fun agregarAlCarrito(@Body request: Map<String, Int>): Call<Map<String, String>>


    // --- RUTAS DE ADMINISTRADOR ---

    /**
     * Crea un nuevo producto (Admin)
     * POST /api/inventory
     */
    @POST("api/inventory")
    fun createProduct(@Body product: CreateProductRequest): Call<Map<String, String>>

    /**
     * Obtiene la lista de todos los usuarios (Admin)
     * GET /admin/users
     */
    @GET("admin/users")
    fun adminGetUsers(): Call<List<User>>

    /**
     * Actualiza el rol de un usuario (Admin)
     * PUT /admin/users/role
     */
    @PUT("admin/users/role")
    fun adminUpdateUserRole(@Body request: Map<String, String>): Call<Map<String, String>>

    /**
     * Reinicia la contraseña de un usuario (Admin)
     * PUT /admin/users/password
     */
    @PUT("admin/users/password")
    fun adminResetUserPassword(@Body request: Map<String, String>): Call<Map<String, String>>

    /**
     * Elimina un usuario (Admin)
     * DELETE /admin/users/:id
     */
    @DELETE("admin/users/{id}")
    fun adminDeleteUser(@Path("id") userId: Int): Call<Map<String, String>>
}