package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.utils.ImageUtils

class AddProductActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private var currentImagePath: String? = null
    private lateinit var ivProductImage: ImageView

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
    }

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

            // Product Image Section
            val imageSectionLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 0, 24)
            }

            val tvImageLabel = TextView(this).apply {
                text = "Product Image (Optional)"
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 0, 0, 8)
            }

            ivProductImage = ImageView(this).apply {
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setOnClickListener {
                    showImageSourceDialog()
                }
            }

            // Set placeholder image
            setPlaceholderImage()

            val imageButtonsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 0)
            }

            val btnTakePhoto = Button(this).apply {
                text = "📷 Take Photo"
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
                    openCamera()
                }
            }

            val btnChooseGallery = Button(this).apply {
                text = "🖼️ Choose from Gallery"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                setOnClickListener {
                    openGallery()
                }
            }

            imageButtonsLayout.addView(btnTakePhoto)
            imageButtonsLayout.addView(btnChooseGallery)

            imageSectionLayout.addView(tvImageLabel)
            imageSectionLayout.addView(ivProductImage)
            imageSectionLayout.addView(imageButtonsLayout)

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
                text = "📦 LIST PRODUCT"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                setOnClickListener {
                    val title = etTitle.text.toString().trim()
                    val description = etDescription.text.toString().trim()
                    val priceText = etPrice.text.toString().trim()
                    val category = spinnerCategory.selectedItem.toString()

                    if (validateInputs(title, description, priceText, category)) {
                        val price = priceText.toDouble()
                        addProduct(title, description, price, category, currentImagePath)
                    }
                }
            }

            // Back Button
            val btnBack = Button(this).apply {
                text = "← BACK TO PRODUCTS"
                setBackgroundColor(Color.parseColor("#757575"))
                setTextColor(Color.WHITE)
                setPadding(0, 20, 0, 20)
                setOnClickListener {
                    finish()
                }
            }

            layout.addView(tvTitle)
            layout.addView(imageSectionLayout)
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

    private fun setPlaceholderImage() {
        ivProductImage.setImageResource(android.R.drawable.ic_menu_camera)
        ivProductImage.setColorFilter(Color.parseColor("#CCCCCC"))
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Remove Photo", "Cancel")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Product Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> removeCurrentImage()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Gallery error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeCurrentImage() {
        currentImagePath = null
        setPlaceholderImage()
        Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    handleCameraResult(data)
                }
                GALLERY_REQUEST_CODE -> {
                    handleGalleryResult(data)
                }
            }
        }
    }

    private fun handleCameraResult(data: Intent?) {
        try {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                currentImagePath = ImageUtils.saveImageToInternalStorage(this, imageBitmap)
                if (currentImagePath != null) {
                    ivProductImage.setImageBitmap(imageBitmap)
                    ivProductImage.clearColorFilter()
                    Toast.makeText(this, "Photo captured successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to capture photo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing photo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleGalleryResult(data: Intent?) {
        try {
            data?.data?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                currentImagePath = ImageUtils.saveImageToInternalStorage(this, bitmap)
                if (currentImagePath != null) {
                    ivProductImage.setImageBitmap(bitmap)
                    ivProductImage.clearColorFilter()
                    Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
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
        if (price.toDoubleOrNull() == null) {
            Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun addProduct(title: String, description: String, price: Double, category: String, imagePath: String?) {
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
                imagePath = imagePath ?: "",
                sellerPhone = userPhone,
                sellerName = userName,
                location = userLocation
            )

            val productId = dbHelper.addProduct(product)
            if (productId != -1L) {
                Toast.makeText(this, "✅ Product listed successfully!", Toast.LENGTH_LONG).show()
                finish()
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