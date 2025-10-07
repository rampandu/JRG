package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.utils.ImageUtils
import com.ram.farmersmarket.adapters.ProductAdapter

class MyProductsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currentUserPhone: String
    private lateinit var adapter: ProductAdapter
    private var userProducts: List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize database and shared preferences
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)
            currentUserPhone = sharedPref.getString("current_user_phone", "") ?: ""

            if (currentUserPhone.isEmpty()) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            // Create layout programmatically
            val scrollView = ScrollView(this)
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
            }

            // Header with back button
            val headerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setPadding(32, 32, 32, 32)
            }

            val btnBack = Button(this).apply {
                text = "← Back"
                setBackgroundColor(Color.TRANSPARENT)
                setTextColor(Color.WHITE)
                setOnClickListener {
                    finish()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvTitle = TextView(this).apply {
                text = "📦 My Products"
                textSize = 18f
                setTextColor(Color.WHITE)
                setPadding(32, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            headerLayout.addView(btnBack)
            headerLayout.addView(tvTitle)

            // Content area
            val contentLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
            }

            // Add Product Button
            val btnAddProduct = Button(this).apply {
                text = "➕ Add New Product"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                setOnClickListener {
                    val intent = Intent(this@MyProductsActivity, AddProductActivity::class.java)
                    startActivity(intent)
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Empty state message
            val tvEmptyState = TextView(this).apply {
                text = "You haven't listed any products yet.\n\nTap the button above to add your first product!"
                textSize = 16f
                setTextColor(Color.GRAY)
                setPadding(0, 50, 0, 50)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // RecyclerView for products
            val recyclerView = RecyclerView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutManager = LinearLayoutManager(this@MyProductsActivity)
            }

            contentLayout.addView(btnAddProduct)
            contentLayout.addView(tvEmptyState)
            contentLayout.addView(recyclerView)

            // Add to main layout
            layout.addView(headerLayout)
            layout.addView(contentLayout)
            scrollView.addView(layout)
            setContentView(scrollView)

            // Load user's products
            loadUserProducts()

            // Setup adapter with correct constructor
            adapter = ProductAdapter(userProducts) { product ->
                // Handle item click - open product details
                val intent = Intent(this@MyProductsActivity, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT", product)
                startActivity(intent)
            }
            recyclerView.adapter = adapter

            // Update empty state
            updateEmptyState(tvEmptyState, recyclerView)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun loadUserProducts() {
        try {
            userProducts = dbHelper.getProductsBySeller(currentUserPhone)
            adapter.products = userProducts
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState(emptyStateView: TextView, recyclerView: RecyclerView) {
        if (userProducts.isEmpty()) {
            emptyStateView.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        } else {
            emptyStateView.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload products when returning from AddProductActivity
        loadUserProducts()

        // Update empty state
        val scrollView = findViewById<ScrollView>(android.R.id.content)
        val contentLayout = scrollView?.getChildAt(0) as? LinearLayout
        val emptyState = contentLayout?.getChildAt(1)?.findViewById<TextView>(android.R.id.text1)
        val recyclerView = contentLayout?.getChildAt(1)?.findViewById<RecyclerView>(android.R.id.list)

        if (emptyState != null && recyclerView != null) {
            updateEmptyState(emptyState, recyclerView)
        }
    }

    private fun showErrorScreen(e: Exception) {
        val errorLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(100, 100, 100, 100)
        }

        val errorText = TextView(this).apply {
            text = "Error: ${e.message}"
            textSize = 16f
            setTextColor(Color.RED)
        }

        errorLayout.addView(errorText)
        setContentView(errorLayout)
    }
}