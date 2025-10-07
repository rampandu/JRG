package com.ram.farmersmarket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ram.farmersmarket.database.DatabaseHelper
import com.ram.farmersmarket.utils.LocationUtils

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currentUserPhone: String
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private var selectedAddress: String = ""
    private lateinit var tvAddress: TextView

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
                text = "📍 Set Your Location"
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

            // Address display
            tvAddress = TextView(this).apply {
                text = "Tap on map to select your location"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(32, 16, 32, 16)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Map fragment container - Create with a unique ID
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

            val btnUseCurrent = Button(this).apply {
                text = "📍 Use Current"
                setBackgroundColor(Color.parseColor("#2196F3"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    useCurrentLocation()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
            }

            val btnSaveLocation = Button(this).apply {
                text = "💾 Save Location"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    saveLocation()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            controlsLayout.addView(btnUseCurrent)
            controlsLayout.addView(btnSaveLocation)

            mainLayout.addView(headerLayout)
            mainLayout.addView(tvAddress)
            mainLayout.addView(mapContainer)
            mainLayout.addView(controlsLayout)
            setContentView(mainLayout)

            // Initialize map with the container ID
            initializeMap(mapContainer.id)

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
            Toast.makeText(this, "Map initialization failed", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configure map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

        // Set default location (India center)
        val defaultLocation = LatLng(20.5937, 78.9629)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4f))

        // Add click listener for map
        googleMap.setOnMapClickListener { latLng ->
            updateSelectedLocation(latLng.latitude, latLng.longitude)
        }

        // Load user's current location if available
        val currentUser = dbHelper.getUser(currentUserPhone)
        currentUser?.let { user ->
            if (user.hasValidLocation()) {
                updateSelectedLocation(user.latitude, user.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(user.latitude, user.longitude), 12f
                ))
            }
        }
    }

    private fun updateSelectedLocation(latitude: Double, longitude: Double) {
        selectedLatitude = latitude
        selectedLongitude = longitude

        // Get address from coordinates
        selectedAddress = LocationUtils.getAddressFromCoordinates(this, latitude, longitude)

        // Update UI
        tvAddress.text = selectedAddress

        // Clear existing markers and add new one
        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title("Your Location")
                .snippet(selectedAddress)
        )
    }

    private fun useCurrentLocation() {
        try {
            // For demo purposes, use a default location
            // In a real app, you would use FusedLocationProviderClient here
            val demoLocation = LatLng(28.6139, 77.2090) // New Delhi
            updateSelectedLocation(demoLocation.latitude, demoLocation.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(demoLocation, 12f))

            Toast.makeText(this, "Using demo location (New Delhi)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Location service not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLocation() {
        if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
            Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val success = dbHelper.updateUserLocation(
                currentUserPhone,
                selectedLatitude,
                selectedLongitude,
                selectedAddress
            )

            if (success) {
                Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_LONG).show()

                // Update shared preferences
                val editor = sharedPref.edit()
                editor.putString("current_user_location", selectedAddress)
                editor.apply()

                finish()
            } else {
                Toast.makeText(this, "Failed to save location", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving location: ${e.message}", Toast.LENGTH_SHORT).show()
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