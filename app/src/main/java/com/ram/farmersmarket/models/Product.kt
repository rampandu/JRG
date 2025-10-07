package com.ram.farmersmarket.models

import java.io.Serializable

data class Product(
    val productId: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imagePath: String = "",
    val sellerPhone: String = "",
    val sellerName: String = "",
    val location: String = "",
    // NEW: Location coordinates for maps
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable {

    // Helper method to check if product has valid coordinates
    fun hasValidLocation(): Boolean {
        return latitude != 0.0 && longitude != 0.0
    }
}