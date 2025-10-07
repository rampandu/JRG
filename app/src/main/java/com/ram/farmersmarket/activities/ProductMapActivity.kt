package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.utils.LocationUtils

class ProductMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currentUserPhone: String
    private var products: List<Product> = listOf()
    private val markers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize database and shared preferences
            dbHelper = DatabaseHelper(this)
            sharedPref = getSharedPreferences("farmers_market", Context.MODE_PRIVATE)
            currentUserPhone = sharedPref.getString("current_user_phone", "") ?: ""

            // Create layout programmatically
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
                setPadding(32, 32, 32, 32)
            }

            val btnBack = Button(this).apply {
                text = "← Back"
                setBackgroundColor(Color.TRANSPARENT)
                setTextColor(Color.WHITE)
                setOnClickListener {
                    finish()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvTitle = TextView(this).apply {
                text = "🗺️ Products Map"
                textSize = 18f
                setTextColor(Color.WHITE)
                setPadding(32, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            headerLayout.addView(btnBack)
            headerLayout.addView(tvTitle)

            // Map fragment container - Create with unique ID
            val mapContainer = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0
                ).apply {
                    weight = 1f
                }
                id = View.generateViewId() // Generate unique ID
            }

            // Controls layout
            val controlsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
            }

            val btnFilterNearby = Button(this).apply {
                text = "📍 Nearby Products"
                setBackgroundColor(Color.parseColor("#2196F3"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    showNearbyProducts()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
            }

            val btnShowAll = Button(this).apply {
                text = "🌍 All Products"
                setBackgroundColor(Color.parseColor("#FF9800"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    showAllProducts()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            controlsLayout.addView(btnFilterNearby)
            controlsLayout.addView(btnShowAll)

            mainLayout.addView(headerLayout)
            mainLayout.addView(mapContainer)
            mainLayout.addView(controlsLayout)
            setContentView(mainLayout)

            // Initialize map with container ID
            initializeMap(mapContainer.id)

            // Load products
            loadProducts()

        } catch (e: Exception) {
            showErrorScreen(e)
        }
    }

    private fun initializeMap(containerId: Int) {
        try {
            val mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(containerId, mapFragment)
                .commit()
            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Map initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configure map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // Set default location (India center)
        val defaultLocation = LatLng(20.5937, 78.9629)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4f))

        // Load products on map
        showAllProducts()
    }

    private fun loadProducts() {
        try {
            products = dbHelper.getAllProducts()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllProducts() {
        clearMarkers()

        val productsWithLocation = products.filter { it.hasValidLocation() }

        if (productsWithLocation.isEmpty()) {
            Toast.makeText(this, "No products with location data available", Toast.LENGTH_LONG).show()
            return
        }

        val boundsBuilder = LatLngBounds.Builder()

        productsWithLocation.forEach { product ->
            val productLatLng = LatLng(product.latitude, product.longitude)

            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(productLatLng)
                    .title(product.title)
                    .snippet("₹${product.price} • ${product.category}")
                    .icon(BitmapDescriptorFactory.defaultMarker(getCategoryColor(product.category)))
            )

            marker?.tag = product
            marker?.let { markers.add(it) }
            boundsBuilder.include(productLatLng)
        }

        // Zoom to show all markers
        try {
            val bounds = boundsBuilder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            // If only one marker, zoom to it
            if (productsWithLocation.isNotEmpty()) {
                val firstProduct = productsWithLocation.first()
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(firstProduct.latitude, firstProduct.longitude), 12f
                    )
                )
            }
        }

        // Add marker click listener
        googleMap.setOnMarkerClickListener { marker ->
            val product = marker.tag as? Product
            product?.let {
                showProductInfo(it)
            }
            true
        }
    }

    private fun showNearbyProducts() {
        val currentUser = dbHelper.getUser(currentUserPhone)

        if (currentUser == null || !currentUser.hasValidLocation()) {
            Toast.makeText(this, "Please set your location in profile to see nearby products", Toast.LENGTH_LONG).show()
            return
        }

        clearMarkers()

        val nearbyProducts = dbHelper.getProductsNearLocation(
            currentUser.latitude,
            currentUser.longitude,
            50.0
        )

        if (nearbyProducts.isEmpty()) {
            Toast.makeText(this, "No products found within 50km", Toast.LENGTH_LONG).show()
            return
        }

        val boundsBuilder = LatLngBounds.Builder()

        // Add user location marker
        val userLatLng = LatLng(currentUser.latitude, currentUser.longitude)
        val userMarker = googleMap.addMarker(
            MarkerOptions()
                .position(userLatLng)
                .title("Your Location")
                .snippet(currentUser.location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        userMarker?.let { markers.add(it) }
        boundsBuilder.include(userLatLng)

        // Add product markers
        nearbyProducts.forEach { product ->
            val productLatLng = LatLng(product.latitude, product.longitude)

            val distance = LocationUtils.calculateDistance(
                currentUser.latitude, currentUser.longitude,
                product.latitude, product.longitude
            )

            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(productLatLng)
                    .title(product.title)
                    .snippet("${LocationUtils.formatDistance(distance)} • ₹${product.price}")
                    .icon(BitmapDescriptorFactory.defaultMarker(getCategoryColor(product.category)))
            )

            marker?.tag = product
            marker?.let { markers.add(it) }
            boundsBuilder.include(productLatLng)
        }

        // Zoom to show all markers
        try {
            val bounds = boundsBuilder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f))
        }

        googleMap.setOnMarkerClickListener { marker ->
            val product = marker.tag as? Product
            product?.let {
                showProductInfo(it)
            }
            true
        }
    }

    private fun showProductInfo(product: Product) {
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle(product.title)
        dialog.setMessage(
            """
            Price: ₹${product.price}
            Category: ${product.category}
            Seller: ${product.sellerName}
            Location: ${product.location}
            
            ${product.description}
            """.trimIndent()
        )
        dialog.setPositiveButton("View Details") { _, _ ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT", product)
            startActivity(intent)
        }
        dialog.setNegativeButton("Get Directions") { _, _ ->
            openDirections(product.latitude, product.longitude)
        }
        dialog.setNeutralButton("Close", null)
        dialog.show()
    }

    private fun openDirections(latitude: Double, longitude: Double) {
        try {
            val uri = "http://maps.google.com/maps?daddr=$latitude,$longitude"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearMarkers() {
        markers.forEach { it.remove() }
        markers.clear()
    }

    private fun getCategoryColor(category: String): Float {
        return when (category) {
            "Livestock" -> BitmapDescriptorFactory.HUE_GREEN
            "Vegetables" -> BitmapDescriptorFactory.HUE_GREEN
            "Fruits" -> BitmapDescriptorFactory.HUE_ORANGE
            "Grains" -> BitmapDescriptorFactory.HUE_ORANGE
            "Equipment" -> BitmapDescriptorFactory.HUE_BLUE
            "Poultry" -> BitmapDescriptorFactory.HUE_RED
            "Dairy" -> BitmapDescriptorFactory.HUE_VIOLET
            else -> BitmapDescriptorFactory.HUE_YELLOW
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