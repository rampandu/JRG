package com.ram.farmersmarket.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ram.farmersmarket.adapters.ProductAdapter
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var emptyState: TextView
    private lateinit var etSearch: EditText
    private lateinit var spinnerCategory: Spinner

    private var allProducts: List<Product> = listOf()
    private val categories = arrayOf("All Categories", "Livestock", "Vegetables", "Fruits", "Grains", "Equipment", "Poultry", "Dairy", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create main layout with fixed bottom buttons
            val mainLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

            // Header - Fixed at top
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
                text = "Find Local Farm Products"
                textSize = 14f
                setTextColor(Color.WHITE)
            }

            headerLayout.addView(tvTitle)
            headerLayout.addView(tvSubtitle)

            // Search Bar - Fixed below header
            val searchLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 8)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
            }

            // Search input with clear button
            val searchInputLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            etSearch = EditText(this).apply {
                hint = "🔍 Search products..."
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }

                maxLines = 1
                isSingleLine = true
                inputType = android.text.InputType.TYPE_CLASS_TEXT
                imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.hideSoftInputFromWindow(windowToken, 0)
                        return@setOnEditorActionListener true
                    }
                    false
                }
            }

            val btnClearSearch = Button(this).apply {
                text = "✕"
                setBackgroundColor(Color.parseColor("#757575"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    etSearch.text.clear()
                    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                }
            }

            searchInputLayout.addView(etSearch)
            searchInputLayout.addView(btnClearSearch)

            // Category Filter
            val categoryLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 0)
            }

            val tvCategoryLabel = TextView(this).apply {
                text = "Filter:"
                textSize = 14f
                setTextColor(Color.BLACK)
                setPadding(0, 0, 8, 0)
            }

            spinnerCategory = Spinner(this).apply {
                adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categories)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            categoryLayout.addView(tvCategoryLabel)
            categoryLayout.addView(spinnerCategory)

            searchLayout.addView(searchInputLayout)
            searchLayout.addView(categoryLayout)

            // Scrollable content area for products
            val scrollView = ScrollView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0, // 0 height with weight
                    1f // Takes all remaining space between header and buttons
                )
            }

            val scrollContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // RecyclerView for products
            recyclerView = RecyclerView(this).apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Empty state
            emptyState = TextView(this).apply {
                text = "No products found\nTry changing your search or be the first to list something!"
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 50, 0, 50)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            scrollContent.addView(recyclerView)
            scrollContent.addView(emptyState)
            scrollView.addView(scrollContent)

            // Fixed Bottom Buttons - Always visible
            val buttonsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.parseColor("#FAFAFA"))
                elevation = 8f // Shadow effect
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Add Product Button
            val btnAddProduct = Button(this).apply {
                text = "ADD PRODUCT"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
                }
            }

            // Refresh Button
            val btnRefresh = Button(this).apply {
                text = "REFRESH"
                setBackgroundColor(Color.parseColor("#2196F3"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
                setOnClickListener {
                    loadProducts()
                    Toast.makeText(this@MainActivity, "List refreshed!", Toast.LENGTH_SHORT).show()
                }
            }

            // Logout Button
            val btnBack = Button(this).apply {
                text = "LOGOUT"
                setBackgroundColor(Color.parseColor("#F44336"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                setOnClickListener {
                    finish()
                }
            }

            buttonsLayout.addView(btnAddProduct)
            buttonsLayout.addView(btnRefresh)
            buttonsLayout.addView(btnBack)

            // Add all views to main layout in correct order
            mainLayout.addView(headerLayout)
            mainLayout.addView(searchLayout)
            mainLayout.addView(scrollView) // Scrollable content area
            mainLayout.addView(buttonsLayout) // Fixed bottom buttons

            setContentView(mainLayout)

            // Initialize database and load products
            dbHelper = DatabaseHelper(this)
            setupSearchAndFilter()
            loadProducts()

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent): Boolean {
        if (ev.action == android.view.MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = android.graphics.Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setupSearchAndFilter() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterProducts()
            }
        })

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterProducts()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadProducts() {
        try {
            allProducts = dbHelper.getAllProducts()
            filterProducts()

        } catch (e: Exception) {
            showError("Error loading products: ${e.message}")
        }
    }

    private fun filterProducts() {
        val searchQuery = etSearch.text.toString().trim().lowercase()
        val selectedCategory = spinnerCategory.selectedItem.toString()

        val filteredProducts = allProducts.filter { product ->
            val matchesSearch = searchQuery.isEmpty() ||
                    product.title.lowercase().contains(searchQuery) ||
                    product.description.lowercase().contains(searchQuery) ||
                    product.category.lowercase().contains(searchQuery)

            val matchesCategory = selectedCategory == "All Categories" ||
                    product.category == selectedCategory

            matchesSearch && matchesCategory
        }

        updateProductList(filteredProducts)
    }

    private fun updateProductList(products: List<Product>) {
        if (products.isNotEmpty()) {
            if (::adapter.isInitialized) {
                adapter.updateProducts(products)
            } else {
                adapter = ProductAdapter(products)
                recyclerView.adapter = adapter
            }

            emptyState.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE

        } else {
            emptyState.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
            emptyState.text = if (allProducts.isEmpty()) {
                "No products available\nBe the first to list something!"
            } else {
                "No products match your search\nTry different keywords or categories"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
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