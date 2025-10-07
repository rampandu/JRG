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

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currentUserPhone: String
    private lateinit var currentUser: User

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

            // Get current user data
            currentUser = dbHelper.getUser(currentUserPhone) ?: run {
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
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
                text = "←"
                setBackgroundColor(Color.TRANSPARENT)
                setTextColor(Color.WHITE)
                setOnClickListener {
                    finish()
                }
            }

            val tvTitle = TextView(this).apply {
                text = "👤 My Profile"
                textSize = 18f
                setTextColor(Color.WHITE)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = 16
                }
            }

            headerLayout.addView(btnBack)
            headerLayout.addView(tvTitle)

            // Content area
            val contentLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
            }

            // Profile Card
            val profileCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#F8F9FA"))
                setPadding(32, 32, 32, 32)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24
                }
            }

            val tvUserName = TextView(this).apply {
                text = currentUser.name
                textSize = 24f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8
                }
            }

            val tvUserPhone = TextView(this).apply {
                text = "📱 ${currentUser.phone}"
                textSize = 16f
                setTextColor(Color.DKGRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8
                }
            }

            val tvUserLocation = TextView(this).apply {
                text = "📍 ${currentUser.location}"
                textSize = 16f
                setTextColor(Color.DKGRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            // Location status
            val locationStatus = TextView(this).apply {
                text = if (currentUser.hasValidLocation()) {
                    "✅ Location set for maps and nearby products"
                } else {
                    "❌ Location not set - Set location for better experience"
                }
                textSize = 14f
                setTextColor(if (currentUser.hasValidLocation()) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800"))
                setPadding(0, 8, 0, 0)
            }

            profileCard.addView(tvUserName)
            profileCard.addView(tvUserPhone)
            profileCard.addView(tvUserLocation)
            profileCard.addView(locationStatus)

            // Stats Section
            val statsLabel = TextView(this).apply {
                text = "My Stats"
                textSize = 18f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
            }

            val statsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24
                }
            }

            // Products Count
            val productsCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#E3F2FD"))
                setPadding(24, 24, 24, 24)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
                gravity = android.view.Gravity.CENTER
            }

            val productsCount = dbHelper.getProductsBySeller(currentUserPhone).size

            val tvProductsCount = TextView(this).apply {
                text = productsCount.toString()
                textSize = 24f
                setTextColor(Color.parseColor("#2196F3"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvProductsLabel = TextView(this).apply {
                text = "Listed Products"
                textSize = 12f
                setTextColor(Color.DKGRAY)
            }

            productsCard.addView(tvProductsCount)
            productsCard.addView(tvProductsLabel)

            // Since we skipped favorites, let's add a placeholder for future features
            val activityCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#E8F5E8"))
                setPadding(24, 24, 24, 24)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = android.view.Gravity.CENTER
            }

            val tvActivityCount = TextView(this).apply {
                text = "0"
                textSize = 24f
                setTextColor(Color.parseColor("#4CAF50"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvActivityLabel = TextView(this).apply {
                text = "Active"
                textSize = 12f
                setTextColor(Color.DKGRAY)
            }

            activityCard.addView(tvActivityCount)
            activityCard.addView(tvActivityLabel)

            statsLayout.addView(productsCard)
            statsLayout.addView(activityCard)

            // Action Buttons
            val buttonsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            // Edit Profile Button
            val btnEditProfile = Button(this).apply {
                text = "✏️ Edit Profile"
                setBackgroundColor(Color.parseColor("#2196F3"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
                setOnClickListener {
                    // For now, just show a message
                    Toast.makeText(this@ProfileActivity, "Edit profile feature coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Set Location Button
            val btnSetLocation = Button(this).apply {
                text = "📍 Set Location"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
                setOnClickListener {
                    val intent = Intent(this@ProfileActivity, LocationPickerActivity::class.java)
                    startActivity(intent)
                }
            }

            // My Products Button
            val btnMyProducts = Button(this).apply {
                text = "📦 My Products"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
                setOnClickListener {
                    val intent = Intent(this@ProfileActivity, MyProductsActivity::class.java)
                    startActivity(intent)
                }
            }

            // Logout Button
            val btnLogout = Button(this).apply {
                text = "🚪 Logout"
                setBackgroundColor(Color.parseColor("#F44336"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    logoutUser()
                }
            }

            buttonsLayout.addView(btnEditProfile)
            buttonsLayout.addView(btnSetLocation)
            buttonsLayout.addView(btnMyProducts)
            buttonsLayout.addView(btnLogout)

            // Add all views to content layout
            contentLayout.addView(profileCard)
            contentLayout.addView(statsLabel)
            contentLayout.addView(statsLayout)
            contentLayout.addView(buttonsLayout)

            // Add to main layout
            layout.addView(headerLayout)
            layout.addView(contentLayout)
            scrollView.addView(layout)
            setContentView(scrollView)

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun logoutUser() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Clear shared preferences
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            // Navigate to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity() // Close all activities
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh user data when returning from other activities
        refreshUserData()
    }

    private fun refreshUserData() {
        try {
            val updatedUser = dbHelper.getUser(currentUserPhone)
            updatedUser?.let {
                currentUser = it
                // You could update the UI here if needed
            }
        } catch (e: Exception) {
            // Ignore errors during refresh
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