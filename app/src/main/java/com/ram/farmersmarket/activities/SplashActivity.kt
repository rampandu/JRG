package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create a simple splash screen layout programmatically
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setPadding(64, 64, 64, 64)
            }

            val tvAppName = TextView(this).apply {
                text = "🚜 Farmers Market"
                textSize = 32f
                setTextColor(Color.WHITE)
                setPadding(0, 100, 0, 0)
            }

            val tvTagline = TextView(this).apply {
                text = "Local Farmers • Fresh Products"
                textSize = 16f
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 0)
            }

            layout.addView(tvAppName)
            layout.addView(tvTagline)
            setContentView(layout)

            // Initialize shared preferences
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)

            // Wait for 2 seconds then navigate to appropriate screen
            Handler(Looper.getMainLooper()).postDelayed({
                checkUserAndNavigate()
            }, 2000)

        } catch (e: Exception) {
            // If anything fails, just go to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkUserAndNavigate() {
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}