package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ram.farmersmarket.adapters.ProductAdapter
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var emptyState: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserLocation: TextView
    private lateinit var tvProductsCount: TextView

    private var userProducts: List<Product> = listOf()
    private lateinit var currentUserPhone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create main layout
            val mainLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

            // Header with back button
            val headerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setPadding(16, 16, 16, 16)
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
                text = "My Profile"
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

            // User Info Section
            val userInfoLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#E8F5E8"))
                setPadding(24, 24, 24, 24)
            }

            val tvProfileLabel = TextView(this).apply {
                text = "👤 YOUR PROFILE"
                textSize = 16f
                setTextColor(Color.DKGRAY)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            tvUserName = TextView(this).apply {
                text = "Loading..."
                textSize = 20f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            tvUserLocation = TextView(this).apply {
                text = "📍 Loading..."
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            val userStatsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 16, 0, 0)
            }

            tvProductsCount = TextView(this).apply {
                text = "📦 0 Products"
                textSize = 14f
                setTextColor(Color.parseColor("#4CAF50"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val tvMemberSince = TextView(this).apply {
                text = "⭐ Active Seller"
                textSize = 14f
                setTextColor(Color.parseColor("#FF9800"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            userStatsLayout.addView(tvProductsCount)
            userStatsLayout.addView(tvMemberSince)

            userInfoLayout.addView(tvProfileLabel)
            userInfoLayout.addView(tvUserName)
            userInfoLayout.addView(tvUserLocation)
            userInfoLayout.addView(userStatsLayout)

            // My Products Section
            val productsHeaderLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 24, 24, 16)
            }

            val tvMyProducts = TextView(this).apply {
                text = "MY PRODUCTS"
                textSize = 16f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val btnAddNew = Button(this).apply {
                text = "+ ADD NEW"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(16, 8, 16, 8)
                textSize = 12f
                setOnClickListener {
                    startActivity(Intent(this@ProfileActivity, AddProductActivity::class.java))
                }
            }

            productsHeaderLayout.addView(tvMyProducts)
            productsHeaderLayout.addView(btnAddNew)

            // RecyclerView for user's products
            recyclerView = RecyclerView(this).apply {
                layoutManager = LinearLayoutManager(this@ProfileActivity)
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0, // 0 height with weight
                    1f // Takes remaining space
                )
            }

            // Empty state for user products
            emptyState = TextView(this).apply {
                text = "You haven't listed any products yet!\nTap 'ADD NEW' to get started."
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 50, 0, 50)
            }

            // Action Buttons
            val actionButtonsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.parseColor("#FAFAFA"))
            }

            val btnEditProfile = Button(this).apply {
                text = "✏️ EDIT PROFILE"
                setBackgroundColor(Color.parseColor("#2196F3"))
                setTextColor(Color.WHITE)
                setPadding(0, 16, 0, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8
                }
                setOnClickListener {
                    showEditProfileDialog()
                }
            }

            val btnBackToHome = Button(this).apply {
                text = "🏠 BACK TO MARKET"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setPadding(0, 16, 0, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    finish()
                }
            }

            actionButtonsLayout.addView(btnEditProfile)
            actionButtonsLayout.addView(btnBackToHome)

            // Add all views to main layout
            mainLayout.addView(headerLayout)
            mainLayout.addView(userInfoLayout)
            mainLayout.addView(productsHeaderLayout)
            mainLayout.addView(recyclerView)
            mainLayout.addView(emptyState)
            mainLayout.addView(actionButtonsLayout)

            setContentView(mainLayout)

            // Initialize database and load user data
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)
            loadUserData()
            loadUserProducts()

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun loadUserData() {
        try {
            val userName = sharedPref.getString("current_user_name", "User") ?: "User"
            val userLocation = sharedPref.getString("current_user_location", "Unknown Location") ?: "Unknown Location"
            currentUserPhone = sharedPref.getString("current_user_phone", "") ?: ""

            tvUserName.text = userName
            tvUserLocation.text = "📍 $userLocation"

        } catch (e: Exception) {
            showError("Error loading user data")
        }
    }

    private fun loadUserProducts() {
        try {
            if (currentUserPhone.isNotEmpty()) {
                userProducts = dbHelper.getUserProducts(currentUserPhone)
                updateProductsList()
                tvProductsCount.text = "📦 ${userProducts.size} Products"
            }
        } catch (e: Exception) {
            showError("Error loading your products")
        }
    }

    private fun updateProductsList() {
        if (userProducts.isNotEmpty()) {
            if (::adapter.isInitialized) {
                adapter.updateProducts(userProducts)
            } else {
                adapter = ProductAdapter(userProducts) { product ->
                    // Show product options (View, Delete)
                    showProductOptionsDialog(product)
                }
                recyclerView.adapter = adapter
            }

            emptyState.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE

        } else {
            emptyState.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        }
    }

    private fun showProductOptionsDialog(product: Product) {
        val options = arrayOf("View Details", "Delete Product")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Product Options")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // View Details
                    val intent = Intent(this, ProductDetailActivity::class.java)
                    intent.putExtra("PRODUCT", product)
                    startActivity(intent)
                }
                1 -> {
                    // Delete Product
                    showDeleteConfirmationDialog(product)
                }
            }
        }
        builder.show()
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Product")
        builder.setMessage("Are you sure you want to delete '${product.title}'? This action cannot be undone.")
        builder.setPositiveButton("DELETE") { dialog, which ->
            deleteProduct(product)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    private fun deleteProduct(product: Product) {
        try {
            val success = dbHelper.deleteProduct(product.id)
            if (success) {
                Toast.makeText(this, "Product deleted successfully!", Toast.LENGTH_SHORT).show()
                loadUserProducts() // Refresh the list
            } else {
                showError("Failed to delete product")
            }
        } catch (e: Exception) {
            showError("Error deleting product: ${e.message}")
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val etName = EditText(this).apply {
            hint = "Your Name"
            setText(sharedPref.getString("current_user_name", ""))
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        val etLocation = EditText(this).apply {
            hint = "Your Location/Village"
            setText(sharedPref.getString("current_user_location", ""))
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        dialogView.addView(etName)
        dialogView.addView(etLocation)

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Edit Profile")
        builder.setView(dialogView)
        builder.setPositiveButton("SAVE") { dialog, which ->
            val newName = etName.text.toString().trim()
            val newLocation = etLocation.text.toString().trim()

            if (newName.isNotEmpty() && newLocation.isNotEmpty()) {
                updateUserProfile(newName, newLocation)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    private fun updateUserProfile(newName: String, newLocation: String) {
        try {
            with(sharedPref.edit()) {
                putString("current_user_name", newName)
                putString("current_user_location", newLocation)
                apply()
            }

            // Update UI
            tvUserName.text = newName
            tvUserLocation.text = "📍 $newLocation"

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            showError("Error updating profile")
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProducts() // Refresh when returning from AddProductActivity
    }

    private fun showError(message: String) {
        Toast.makeText(this, "❌ $message", Toast.LENGTH_LONG).show()
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