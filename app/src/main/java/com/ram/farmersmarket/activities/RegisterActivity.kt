package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.User
import com.ram.farmersmarket.utils.LocationUtils

class RegisterActivity : AppCompatActivity() {

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
                setPadding(64, 64, 64, 64)
            }

            // App Title
            val tvAppTitle = TextView(this).apply {
                text = "🚜 Farmers Market"
                textSize = 28f
                setTextColor(Color.parseColor("#4CAF50"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 0, 0, 32)
                gravity = android.view.Gravity.CENTER
            }

            // Register Title
            val tvRegisterTitle = TextView(this).apply {
                text = "Create New Account"
                textSize = 20f
                setTextColor(Color.BLACK)
                setPadding(0, 0, 0, 32)
                gravity = android.view.Gravity.CENTER
            }

            // Name Input
            val etName = EditText(this).apply {
                hint = "Full Name"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Phone Input
            val etPhone = EditText(this).apply {
                hint = "Phone Number"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                inputType = android.text.InputType.TYPE_CLASS_PHONE
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Location Input
            val etLocation = EditText(this).apply {
                hint = "Your Location (City/Village)"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Password Input
            val etPassword = EditText(this).apply {
                hint = "Password"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24
                }
            }

            // Confirm Password Input
            val etConfirmPassword = EditText(this).apply {
                hint = "Confirm Password"
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 32
                }
            }

            // Register Button
            val btnRegister = Button(this).apply {
                text = "📝 REGISTER"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                setOnClickListener {
                    val name = etName.text.toString().trim()
                    val phone = etPhone.text.toString().trim()
                    val location = etLocation.text.toString().trim()
                    val password = etPassword.text.toString().trim()
                    val confirmPassword = etConfirmPassword.text.toString().trim()

                    registerUser(name, phone, location, password, confirmPassword)
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Login Link
            val tvLoginLink = TextView(this).apply {
                text = "Already have an account? Login here"
                textSize = 14f
                setTextColor(Color.parseColor("#2196F3"))
                gravity = android.view.Gravity.CENTER
                setPadding(0, 16, 0, 0)
                setOnClickListener {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            layout.addView(tvAppTitle)
            layout.addView(tvRegisterTitle)
            layout.addView(etName)
            layout.addView(etPhone)
            layout.addView(etLocation)
            layout.addView(etPassword)
            layout.addView(etConfirmPassword)
            layout.addView(btnRegister)
            layout.addView(tvLoginLink)
            scrollView.addView(layout)
            setContentView(scrollView)

            // Initialize database and shared preferences
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun registerUser(name: String, phone: String, location: String, password: String, confirmPassword: String) {
        // Validation
        if (name.isEmpty() || phone.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.length < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Check if user already exists
            val existingUser = dbHelper.getUser(phone)
            if (existingUser != null) {
                Toast.makeText(this, "Phone number already registered", Toast.LENGTH_LONG).show()
                return
            }

            // Get coordinates for location
            val coordinates = LocationUtils.getCoordinatesFromAddress(this, location)
            val latitude = coordinates?.first ?: 0.0
            val longitude = coordinates?.second ?: 0.0

            // Create new user
            val newUser = User(
                name = name,
                phone = phone,
                location = location,
                password = password,
                latitude = latitude,
                longitude = longitude
            )

            // Add user to database
            val success = dbHelper.addUser(newUser)

            if (success) {
                // Registration successful
                saveUserToSharedPref(newUser)
                Toast.makeText(this, "Registration successful! Welcome, $name!", Toast.LENGTH_LONG).show()

                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Registration error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserToSharedPref(user: User) {
        val editor = sharedPref.edit()
        editor.putString("current_user_phone", user.phone)
        editor.putString("current_user_name", user.name)
        editor.putString("current_user_location", user.location)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
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