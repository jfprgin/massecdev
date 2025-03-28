package com.example.loginhttp.model

data class OrderItem(
    val id: Int,
    val timestamp: String,
    val fromLocation: String,
    val toLocation: String,
    val status: String,
    val synced: Boolean,
)
