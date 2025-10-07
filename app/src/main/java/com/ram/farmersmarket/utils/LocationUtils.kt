package com.ram.farmersmarket.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.Locale

object LocationUtils {

    /**
     * Calculate distance between two points in kilometers
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble() / 1000.0 // Convert to kilometers
    }

    /**
     * Format distance for display
     */
    fun formatDistance(km: Double): String {
        return if (km < 1) {
            "${(km * 1000).toInt()} m"
        } else {
            "%.1f km".format(km)
        }
    }

    /**
     * Get address from coordinates using Geocoder
     */
    fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            // Use try-catch for the geocoder call and handle null safely
            val addresses = try {
                geocoder.getFromLocation(latitude, longitude, 1)
            } catch (e: Exception) {
                null
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()

                if (address.locality != null) {
                    sb.append(address.locality)
                }
                if (address.adminArea != null) {
                    if (sb.isNotEmpty()) sb.append(", ")
                    sb.append(address.adminArea)
                }
                if (sb.isEmpty()) {
                    sb.append("$latitude, $longitude")
                }

                sb.toString()
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Location: $latitude, $longitude"
        }
    }

    /**
     * Get coordinates from address using Geocoder
     */
    fun getCoordinatesFromAddress(context: Context, address: String): Pair<Double, Double>? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            // Use try-catch for the geocoder call and handle null safely
            val addresses = try {
                geocoder.getFromLocationName(address, 1)
            } catch (e: Exception) {
                null
            }

            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if location services are available
     */
    fun isLocationAvailable(context: Context): Boolean {
        return try {
            val geocoder = Geocoder(context)
            geocoder.getFromLocationName("test", 1) // Test call
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Validate coordinates
     */
    fun isValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    /**
     * Get approximate distance category
     */
    fun getDistanceCategory(distanceKm: Double): String {
        return when {
            distanceKm < 1 -> "Very Close"
            distanceKm < 5 -> "Close"
            distanceKm < 20 -> "Nearby"
            distanceKm < 50 -> "Within Area"
            else -> "Far"
        }
    }
}