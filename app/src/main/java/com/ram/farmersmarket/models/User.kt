package com.ram.farmersmarket.models

data class User(
    val id: Long = 0,
    val phoneNumber: String = "",
    val name: String = "",
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis()
)