package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product

class LoginActivity : AppCompatActivity() {

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
                setPadding(100, 100, 100, 100)
            }

            val tvTitle = TextView(this).apply {
                text = "Farmers Market"
                textSize = 24f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val etPhoneNumber = EditText(this).apply {
                hint = "Phone (10 digits)"
                setPadding(50, 50, 50, 50)
            }

            val etName = EditText(this).apply {
                hint = "Your Name"
                setPadding(50, 50, 50, 50)
            }

            val etLocation = EditText(this).apply {
                hint = "Your Village"
                setPadding(50, 50, 50, 50)
            }

            val btnLogin = Button(this).apply {
                text = "LOGIN / REGISTER"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(50, 50, 50, 50)
                setOnClickListener {
                    val phone = etPhoneNumber.text.toString().trim()
                    val name = etName.text.toString().trim()
                    val location = etLocation.text.toString().trim()

                    if (phone.length == 10 && name.isNotEmpty() && location.isNotEmpty()) {
                        loginOrRegister(phone, name, location)
                    } else {
                        Toast.makeText(this@LoginActivity, "Please fill all fields (10 digit phone)", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            layout.addView(tvTitle)
            layout.addView(etPhoneNumber)
            layout.addView(etName)
            layout.addView(etLocation)
            layout.addView(btnLogin)
            scrollView.addView(layout)
            setContentView(scrollView)

            // Initialize database
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun loginOrRegister(phone: String, name: String, location: String) {
        try {
            // Check if user exists
            val existingUser = dbHelper.getUserByPhone(phone)

            if (existingUser == null) {
                // New user - register
                val userId = dbHelper.addUser(phone, name, location)
                if (userId != -1L) {
                    Toast.makeText(this, "✅ Registration successful!", Toast.LENGTH_LONG).show()
                    saveUserSession(phone)
                    addTestProduct(phone, name, location)
                } else {
                    Toast.makeText(this, "❌ Registration failed", Toast.LENGTH_LONG).show()
                }
            } else {
                // Existing user - login
                saveUserSession(phone)
                Toast.makeText(this, "✅ Welcome back, ${existingUser.name}!", Toast.LENGTH_LONG).show()
                goToMainActivity()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun addTestProduct(phone: String, name: String, location: String) {
        try {
            val testProducts = listOf(
                Product(
                    title = "Dairy Cow",
                    description = "Healthy Holstein cow, gives 20L milk daily",
                    price = 35000.0,
                    category = "Livestock",
                    sellerPhone = phone,
                    sellerName = name,
                    location = location
                ),
                Product(
                    title = "Organic Tomatoes",
                    description = "Fresh organic tomatoes from our farm",
                    price = 40.0,
                    category = "Vegetables",
                    sellerPhone = phone,
                    sellerName = name,
                    location = location
                ),
                Product(
                    title = "Tractor for Sale",
                    description = "John Deere tractor, good condition, 2 years old",
                    price = 450000.0,
                    category = "Equipment",
                    sellerPhone = phone,
                    sellerName = name,
                    location = location
                ),
                Product(
                    title = "Fresh Eggs",
                    description = "Farm fresh eggs, collected daily",
                    price = 120.0,
                    category = "Poultry",
                    sellerPhone = phone,
                    sellerName = name,
                    location = location
                )
            )

            var successCount = 0
            testProducts.forEach { product ->
                val productId = dbHelper.addProduct(product)
                if (productId != -1L) {
                    successCount++
                }
            }

            Toast.makeText(this, "✅ $successCount test products added!", Toast.LENGTH_LONG).show()
            goToMainActivity()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Some products failed, but continuing...", Toast.LENGTH_LONG).show()
            goToMainActivity()
        }
    }

    private fun saveUserSession(phone: String) {
        with(sharedPref.edit()) {
            putString("current_user_phone", phone)
            apply()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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