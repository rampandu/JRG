package com.ram.farmersmarket.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ram.farmersmarket.adapters.ProductAdapter
import com.ram.farmersmarket.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create layout programmatically
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
            }

            // Header
            val headerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setPadding(32, 32, 32, 32)
            }

            val tvTitle = TextView(this).apply {
                text = "Farmers Market"
                textSize = 24f
                setTextColor(Color.WHITE)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvSubtitle = TextView(this).apply {
                text = "Local Farm Products"
                textSize = 14f
                setTextColor(Color.WHITE)
            }

            headerLayout.addView(tvTitle)
            headerLayout.addView(tvSubtitle)

            // RecyclerView for products
            recyclerView = RecyclerView(this).apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setPadding(16, 16, 16, 16)
            }

            // Empty state
            val emptyState = TextView(this).apply {
                text = "No products available\nBe the first to list something!"
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 100, 0, 0)
            }

            // Add Product Button
            val btnAddProduct = Button(this).apply {
                text = "ADD YOUR PRODUCT"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
                }
            }

            // Logout Button
            val btnBack = Button(this).apply {
                text = "LOGOUT"
                setBackgroundColor(Color.parseColor("#F44336"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    finish() // Go back to login
                }
            }

            layout.addView(headerLayout)
            layout.addView(recyclerView)
            layout.addView(emptyState)
            layout.addView(btnAddProduct)
            layout.addView(btnBack)
            setContentView(layout)

            // Initialize database and load products
            dbHelper = DatabaseHelper(this)
            loadProducts(emptyState)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh product list when returning from AddProductActivity
        val emptyState = findViewById<TextView>(android.R.id.text2) ?: return
        loadProducts(emptyState)
    }

    private fun loadProducts(emptyState: TextView) {
        try {
            val products = dbHelper.getAllProducts()

            if (products.isNotEmpty()) {
                adapter = ProductAdapter(products)
                recyclerView.adapter = adapter
                emptyState.visibility = android.view.View.GONE
                recyclerView.visibility = android.view.View.VISIBLE
            } else {
                emptyState.visibility = android.view.View.VISIBLE
                recyclerView.visibility = android.view.View.GONE
            }
        } catch (e: Exception) {
            emptyState.text = "Error loading products: ${e.message}"
            emptyState.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
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