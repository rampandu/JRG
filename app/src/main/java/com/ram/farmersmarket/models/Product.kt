package com.ram.farmersmarket.models

data class Product(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imagePath: String = "", // Local file path
    val sellerPhone: String = "",
    val sellerName: String = "",
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "available"
)