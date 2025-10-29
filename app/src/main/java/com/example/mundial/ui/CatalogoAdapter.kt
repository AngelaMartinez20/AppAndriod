package com.example.mundial.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundial.R
import com.example.mundial.databinding.ItemProductoBinding // <- Usa el nuevo layout
import com.example.mundial.network.Producto // <- Usa el modelo Producto

class CatalogoAdapter(
    private var productos: List<Producto>,
    private val onAgregarClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CatalogoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre
            binding.tvPrecioProducto.text = "$${producto.precio}"

            binding.btnAgregar.setOnClickListener {
                onAgregarClickListener(producto.id)
            }

            // --- Cargar la imagen con Glide ---
            val BASE_URL = "https://backappmundial.onrender.com"
            val imageUrl = if (producto.imagen?.startsWith("http") == true) {
                producto.imagen
            } else {
                "$BASE_URL${producto.imagen}"
            }

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.ivProductoImagen)
        }
    }

    // --- Métodos obligatorios del Adapter ---

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductoBinding.inflate(inflater, parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    fun updateProductos(newProductos: List<Producto>) {
        productos = newProductos
        notifyDataSetChanged()
    }
}