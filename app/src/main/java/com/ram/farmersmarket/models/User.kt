package com.ram.farmersmarket.models

import java.io.Serializable

data class User(
    val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val location: String = "",
    val password: String = "",
    // NEW: Location coordinates
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable {

    // Helper method to check if user has valid coordinates
    fun hasValidLocation(): Boolean {
        return latitude != 0.0 && longitude != 0.0
    }
}