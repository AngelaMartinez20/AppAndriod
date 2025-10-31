package com.example.mundial.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mundial.databinding.ItemUserBinding // <-- El binding para item_user.xml
import com.example.mundial.network.User // <-- El modelo de datos del Usuario

/**
 * Este Adapter recibe:
 * 1. Una lista de Usuarios.
 * 2. Tres "funciones de clic" (lambdas) que le dicen qué hacer cuando
 * el admin presiona los botones en una fila.
 */
class UserAdapter(
    private var users: List<User>,
    private val onRoleClick: (User) -> Unit,
    private val onPasswordClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    /**
     * El ViewHolder "sostiene" las vistas de una fila individual (item_user.xml).
     */
    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        // Esta función conecta un objeto 'User' con las vistas del layout
        fun bind(user: User) {
            binding.tvUserName.text = user.username
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = "Rol: ${user.role}"

            // Asignamos los clics de los botones a las funciones (lambdas)
            // que nos pasaron en el constructor.
            binding.btnChangeRole.setOnClickListener { onRoleClick(user) }
            binding.btnResetPass.setOnClickListener { onPasswordClick(user) }
            binding.btnDeleteUser.setOnClickListener { onDeleteClick(user) }
        }
    }

    /**
     * (Requerido) Se llama cuando el RecyclerView necesita crear una nueva fila.
     * Infla (crea) el layout 'item_user.xml'.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    /**
     * (Requerido) Se llama para conectar los datos de un usuario (en 'position')
     * con la fila (el 'holder').
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    /**
     * (Requerido) Le dice al RecyclerView cuántos items hay en la lista.
     */
    override fun getItemCount() = users.size

    /**
     * Función para actualizar la lista de usuarios desde la Activity
     * y notificar al RecyclerView que debe redibujarse.
     */
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}