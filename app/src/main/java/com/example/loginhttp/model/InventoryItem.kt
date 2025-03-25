package com.example.loginhttp.model

data class InventoryItem(
    val id: Int,
    val name: String,
    val timestamp: Long,
    val isSynced: Boolean,
)
