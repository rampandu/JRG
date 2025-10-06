package com.ram.farmersmarket.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*

class LoginActivity : Activity() {  // ← Changed from AppCompatActivity to Activity

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
                text = "TEST BASIC APP"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(50, 50, 50, 50)
                setOnClickListener {
                    Toast.makeText(this@LoginActivity, "✅ App is running!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }

            layout.addView(tvTitle)
            layout.addView(etPhoneNumber)
            layout.addView(etName)
            layout.addView(etLocation)
            layout.addView(btnLogin)
            scrollView.addView(layout)
            setContentView(scrollView)

            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)

        } catch (e: Exception) {
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
}