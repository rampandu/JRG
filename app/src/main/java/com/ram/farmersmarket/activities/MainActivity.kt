package com.ram.farmersmarket.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create layout programmatically
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(100, 100, 100, 100)
            }

            val tvTitle = TextView(this).apply {
                text = "Farmers Market - Main"
                textSize = 24f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            // Initialize database
            dbHelper = DatabaseHelper(this)

            // Get products from database
            val products = dbHelper.getAllProducts()

            val tvStatus = TextView(this).apply {
                text = "Found ${products.size} products in database"
                textSize = 18f
                setTextColor(Color.BLACK)
            }

            // Show product list
            val productList = TextView(this).apply {
                text = if (products.isNotEmpty()) {
                    products.joinToString("\n\n") { product ->
                        "🐄 ${product.title}\n💰 ₹${product.price}\n👨‍🌾 ${product.sellerName}\n📍 ${product.location}"
                    }
                } else {
                    "No products found"
                }
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            val btnBack = Button(this).apply {
                text = "Back to Login"
                setOnClickListener {
                    finish()
                }
            }

            layout.addView(tvTitle)
            layout.addView(tvStatus)
            layout.addView(productList)
            layout.addView(btnBack)
            setContentView(layout)

        } catch (e: Exception) {
            val errorLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(100, 100, 100, 100)
            }

            val errorText = TextView(this).apply {
                text = "MainActivity Error: ${e.message}"
                textSize = 16f
                setTextColor(Color.RED)
            }

            errorLayout.addView(errorText)
            setContentView(errorLayout)
        }
    }
}