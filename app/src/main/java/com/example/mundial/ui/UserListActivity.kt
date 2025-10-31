package com.example.mundial.ui

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mundial.databinding.ActivityUserListBinding
import com.example.mundial.network.ApiService
import com.example.mundial.network.RetrofitClient
import com.example.mundial.network.User
import com.example.mundial.ui.UserAdapter // <-- ¡AÑADE ESTA LÍNEA!
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var apiService: ApiService
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflamos el layout 'activity_user_list.xml'
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Obtener el servicio de API (autenticado)
        apiService = RetrofitClient.getApiService(applicationContext)

        // 2. Configurar el RecyclerView y el Adapter
        setupRecyclerView()

        // 3. Cargar la lista de usuarios desde la API
        loadUsersList()
    }

    /**
     * Configura el RecyclerView con el UserAdapter
     */
    private fun setupRecyclerView() {
        // Inicializamos el adapter pasándole las funciones que debe ejecutar
        // al hacer clic en cada botón (usando method references)
        userAdapter = UserAdapter(
            emptyList(),
            ::handleRoleClick,      // Función para clic en "Rol"
            ::handlePasswordClick,  // Función para clic en "Reset Pass"
            ::handleDeleteClick     // Función para clic en "Borrar"
        )

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity)
            adapter = userAdapter
        }
    }

    /**
     * Llama a la API para obtener la lista de usuarios y la muestra
     */
    private fun loadUsersList() {
        apiService.adminGetUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null) {
                        Log.d("UserListActivity", "Usuarios cargados: ${users.size}")
                        userAdapter.updateUsers(users)
                    }
                } else {
                    Log.e("UserListActivity", "Error al cargar usuarios: ${response.code()}")
                    Toast.makeText(this@UserListActivity, "Error al cargar lista", Toast.LENGTH_SHORT).show()                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("UserListActivity", "Fallo de red al cargar usuarios", t)
                Toast.makeText(this@UserListActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- MANEJADORES DE CLICS DEL ADAPTER ---

    /**
     * Muestra un diálogo para cambiar el ROL de un usuario
     */
    private fun handleRoleClick(user: User) {
        val roles = arrayOf("usuario", "cajero", "mantenimiento", "admin")
        val currentRoleIndex = roles.indexOf(user.role)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Rol para ${user.username}")
            .setSingleChoiceItems(roles, currentRoleIndex) { dialog, which ->
                val nuevoRol = roles[which]
                // Preparamos el cuerpo de la petición: {"id": "15", "role": "cajero"}
                val requestBody = mapOf("id" to user.id.toString(), "role" to nuevoRol)

                apiService.adminUpdateUserRole(requestBody).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@UserListActivity, "Rol actualizado", Toast.LENGTH_SHORT).show()
                            loadUsersList() // Recargamos la lista para ver el cambio
                        } else {
                            Toast.makeText(this@UserListActivity, "Error al actualizar rol", Toast.LENGTH_SHORT).show()                        }
                        dialog.dismiss() // Cierra el diálogo
                    }
                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Toast.makeText(this@UserListActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra un diálogo para REINICIAR LA CONTRASEÑA de un usuario
     */
    private fun handlePasswordClick(user: User) {
        // Crear un EditText para el diálogo
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Nueva Contraseña"
        }

        AlertDialog.Builder(this)
            .setTitle("Reiniciar Contraseña")
            .setMessage("Escribe la nueva contraseña para ${user.username}")
            .setView(input) // Añadir el EditText al diálogo
            .setPositiveButton("Actualizar") { dialog, _ ->
                val nuevaPass = input.text.toString()
                if (nuevaPass.isEmpty()) {
                    Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Preparamos el cuerpo: {"id": "15", "newPassword": "nueva123"}
                val requestBody = mapOf("id" to user.id.toString(), "newPassword" to nuevaPass)

                apiService.adminResetUserPassword(requestBody).enqueue(object : Callback<Map<String,String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@UserListActivity, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@UserListActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Toast.makeText(this@UserListActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra un diálogo para CONFIRMAR LA ELIMINACIÓN de un usuario
     */
    private fun handleDeleteClick(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que quieres eliminar a ${user.username}? Esta acción no se puede deshacer.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Sí, Eliminar") { _, _ ->
                // Llamar a la API para borrar
                apiService.adminDeleteUser(user.id).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@UserListActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            loadUsersList() // Recargamos la lista
                        } else {
                            Toast.makeText(this@UserListActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Toast.makeText(this@UserListActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}