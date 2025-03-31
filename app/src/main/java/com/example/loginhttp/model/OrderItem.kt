package com.example.loginhttp.model

data class OrderItem(
    val id: Int,
    val timestamp: String,
    val fromLocation: String,
    val toLocation: String,
    val status: String,
    val type: OrderType,
    val synced: Boolean,
)

enum class OrderType {
    INTERNAL,
    EXTERNAL
}