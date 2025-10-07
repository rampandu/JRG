package com.ram.farmersmarket.models

import java.io.Serializable

data class Product(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imagePath: String = "", // Add this field for image storage
    val sellerPhone: String = "",
    val sellerName: String = "",
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "available"
) : Serializable