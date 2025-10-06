package com.ram.farmersmarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ram.farmersmarket.R
import com.ram.farmersmarket.models.Product

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvProductTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        private val tvSeller: TextView = itemView.findViewById(R.id.tvSellerName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        fun bind(product: Product) {
            tvTitle.text = product.title
            tvDescription.text = product.description
            tvPrice.text = "₹${product.price}"
            tvCategory.text = product.category
            tvSeller.text = "By ${product.sellerName}"
            tvLocation.text = product.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}