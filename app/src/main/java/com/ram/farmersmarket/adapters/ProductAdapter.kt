package com.ram.farmersmarket.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ram.farmersmarket.R
import com.ram.farmersmarket.activities.ProductDetailActivity
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.utils.ImageUtils

class ProductAdapter(
    var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
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

            // Load product image using Glide
            if (product.imagePath.isNotEmpty() && ImageUtils.isImageFileExists(product.imagePath)) {
                Glide.with(itemView.context)
                    .load(product.imagePath)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .centerCrop()
                    .into(ivProduct)
            } else {
                // Set placeholder if no image
                ivProduct.setImageResource(R.drawable.placeholder_product)
            }

            // Set category-specific colors
            setCategoryColor(product.category)
        }

        private fun setCategoryColor(category: String) {
            val color = when (category) {
                "Livestock" -> Color.parseColor("#8BC34A")
                "Vegetables" -> Color.parseColor("#4CAF50")
                "Fruits" -> Color.parseColor("#FF9800")
                "Grains" -> Color.parseColor("#795548")
                "Equipment" -> Color.parseColor("#607D8B")
                "Poultry" -> Color.parseColor("#FF5722")
                "Dairy" -> Color.parseColor("#2196F3")
                else -> Color.parseColor("#9C27B0")
            }
            tvCategory.setBackgroundColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(product)
        }

        // Optional: Add click animation
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.7f
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    v.alpha = 1.0f
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }
}