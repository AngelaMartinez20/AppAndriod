package com.example.mundial.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundial.R
import com.example.mundial.databinding.ItemCarritoBinding
import com.example.mundial.network.CarritoItem

class CarritoAdapter(
    private var items: List<CarritoItem>
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    /**
     * El ViewHolder: "Sostiene" las vistas de tu layout item_carrito.xml
     */
    inner class CarritoViewHolder(val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            binding.tvNombreProducto.text = item.nombre
            binding.tvPrecioProducto.text = "$${item.precio}"
            binding.tvCantidad.text = "Cantidad: ${item.cantidad}"

            val BASE_URL = "https://backappmundial.onrender.com"
            val imageUrl = if (item.imagen?.startsWith("http") == true) {
                item.imagen
            } else {
                "$BASE_URL${item.imagen}"
            }

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.ivProductoImagen)
        }
    }

    // --- 1. MÉTODO REQUERIDO: onCreateViewHolder ---
    // Se llama para CREAR una nueva fila (ViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCarritoBinding.inflate(inflater, parent, false)
        return CarritoViewHolder(binding)
    }

    // --- 2. MÉTODO REQUERIDO: onBindViewHolder ---
    // Se llama para CONECTAR los datos a una fila (ViewHolder)
    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // --- 3. MÉTODO REQUERIDO: getItemCount ---
    // Se llama para saber CUÁNTOS items hay en la lista
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Función para actualizar la lista de items en el adapter
     */
    fun updateItems(newItems: List<CarritoItem>) {
        items = newItems
        notifyDataSetChanged() // Refresca la lista
    }
}