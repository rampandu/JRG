package com.ram.farmersmarket.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.R
import com.ram.farmersmarket.models.Product

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Get product from intent
            product = intent.getSerializableExtra("PRODUCT") as Product

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
                text = "Product Details"
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
                setPadding(24, 24, 24, 24)
            }

            // Product Title
            val tvProductTitle = TextView(this).apply {
                text = product.title
                textSize = 24f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            // Category Badge
            val tvCategory = TextView(this).apply {
                text = product.category
                textSize = 14f
                setTextColor(Color.WHITE)
                setPadding(12, 6, 12, 6)
                setBackgroundColor(getCategoryColor(product.category))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                    bottomMargin = 16
                }
            }

            // Price
            val tvPrice = TextView(this).apply {
                text = "₹${product.price}"
                textSize = 28f
                setTextColor(Color.parseColor("#4CAF50"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24
                }
            }

            // Description Section
            val tvDescLabel = TextView(this).apply {
                text = "Description"
                textSize = 16f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvDescription = TextView(this).apply {
                text = product.description
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, 8, 0, 24)
            }

            // Seller Information Section
            val sellerSectionLabel = TextView(this).apply {
                text = "Seller Information"
                textSize = 16f
                setTextColor(Color.BLACK)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val sellerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24
                }
            }

            val tvSellerName = TextView(this).apply {
                text = "👨‍🌾 ${product.sellerName}"
                textSize = 16f
                setTextColor(Color.BLACK)
            }

            val tvSellerPhone = TextView(this).apply {
                text = "📞 ${product.sellerPhone}"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            val tvLocation = TextView(this).apply {
                text = "📍 ${product.location}"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            sellerLayout.addView(tvSellerName)
            sellerLayout.addView(tvSellerPhone)
            sellerLayout.addView(tvLocation)

            // Action Buttons
            val buttonsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            // Call Seller Button
            val btnCallSeller = Button(this).apply {
                text = "📞 CALL SELLER"
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
                    callSeller(product.sellerPhone)
                }
            }

            // Message Seller Button
            val btnMessageSeller = Button(this).apply {
                text = "💬 MESSAGE SELLER"
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
                    messageSeller(product.sellerPhone)
                }
            }

            // Share Product Button
            val btnShareProduct = Button(this).apply {
                text = "📤 SHARE PRODUCT"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    shareProduct(product)
                }
            }

            buttonsLayout.addView(btnCallSeller)
            buttonsLayout.addView(btnMessageSeller)
            buttonsLayout.addView(btnShareProduct)

            // Add all views to content layout
            contentLayout.addView(tvProductTitle)
            contentLayout.addView(tvCategory)
            contentLayout.addView(tvPrice)
            contentLayout.addView(tvDescLabel)
            contentLayout.addView(tvDescription)
            contentLayout.addView(sellerSectionLabel)
            contentLayout.addView(sellerLayout)
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

    private fun getCategoryColor(category: String): Int {
        return when (category) {
            "Livestock" -> Color.parseColor("#8BC34A")
            "Vegetables" -> Color.parseColor("#4CAF50")
            "Fruits" -> Color.parseColor("#FF9800")
            "Grains" -> Color.parseColor("#795548")
            "Equipment" -> Color.parseColor("#607D8B")
            "Poultry" -> Color.parseColor("#FF5722")
            "Dairy" -> Color.parseColor("#2196F3")
            else -> Color.parseColor("#9C27B0")
        }
    }

    private fun callSeller(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot make call", Toast.LENGTH_SHORT).show()
        }
    }

    private fun messageSeller(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:$phoneNumber")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open messaging app", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareProduct(product: Product) {
        try {
            val shareText = """
                🚜 Farmers Market - Check out this product!
                
                ${product.title}
                Price: ₹${product.price}
                Category: ${product.category}
                Description: ${product.description}
                Seller: ${product.sellerName}
                Location: ${product.location}
                
                Download Farmers Market app to see more local products!
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, "Share Product"))
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot share product", Toast.LENGTH_SHORT).show()
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