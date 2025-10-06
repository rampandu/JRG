package com.ram.farmersmarket.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product

class AddProductActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create layout programmatically
            val scrollView = ScrollView(this)
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(32, 32, 32, 32)
            }

            val tvTitle = TextView(this).apply {
                text = "Add Your Product"
                textSize = 24f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            // Product Title
            val etTitle = EditText(this).apply {
                hint = "Product Title (e.g., Dairy Cow, Fresh Tomatoes)"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
            }

            // Product Description
            val etDescription = EditText(this).apply {
                hint = "Product Description"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                minLines = 3
            }

            // Price
            val etPrice = EditText(this).apply {
                hint = "Price (₹)"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            // Category Spinner
            val tvCategoryLabel = TextView(this).apply {
                text = "Category"
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 16, 0, 8)
            }

            val categories = arrayOf("Livestock", "Vegetables", "Fruits", "Grains", "Equipment", "Poultry", "Dairy", "Other")
            val spinnerCategory = Spinner(this).apply {
                adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, categories)
            }

            // Submit Button
            val btnSubmit = Button(this).apply {
                text = "LIST PRODUCT"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(50, 20, 50, 20)
                setOnClickListener {
                    val title = etTitle.text.toString().trim()
                    val description = etDescription.text.toString().trim()
                    val priceText = etPrice.text.toString().trim()
                    val category = spinnerCategory.selectedItem.toString()

                    if (validateInputs(title, description, priceText, category)) {
                        val price = priceText.toDouble()
                        addProduct(title, description, price, category)
                    }
                }
            }

            // Back Button
            val btnBack = Button(this).apply {
                text = "BACK TO PRODUCTS"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setPadding(50, 20, 50, 20)
                setOnClickListener {
                    finish()
                }
            }

            layout.addView(tvTitle)
            layout.addView(etTitle)
            layout.addView(etDescription)
            layout.addView(etPrice)
            layout.addView(tvCategoryLabel)
            layout.addView(spinnerCategory)
            layout.addView(btnSubmit)
            layout.addView(btnBack)
            scrollView.addView(layout)
            setContentView(scrollView)

            // Initialize database and shared preferences
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun validateInputs(title: String, description: String, price: String, category: String): Boolean {
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter product title", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter product description", Toast.LENGTH_SHORT).show()
            return false
        }
        if (price.isEmpty()) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show()
            return false
        }
        if (category == "Livestock" && price.toDoubleOrNull() == null) {
            Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun addProduct(title: String, description: String, price: Double, category: String) {
        try {
            // Get current user info from shared preferences
            val userPhone = sharedPref.getString("current_user_phone", "") ?: ""
            val userName = sharedPref.getString("current_user_name", "") ?: ""
            val userLocation = sharedPref.getString("current_user_location", "") ?: ""

            if (userPhone.isEmpty()) {
                Toast.makeText(this, "User not logged in properly", Toast.LENGTH_LONG).show()
                return
            }

            val product = Product(
                title = title,
                description = description,
                price = price,
                category = category,
                sellerPhone = userPhone,
                sellerName = userName,
                location = userLocation
            )

            val productId = dbHelper.addProduct(product)
            if (productId != -1L) {
                Toast.makeText(this, "✅ Product listed successfully!", Toast.LENGTH_LONG).show()
                finish() // Go back to product list
            } else {
                Toast.makeText(this, "❌ Failed to list product", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
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