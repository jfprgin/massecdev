package com.example.loginhttp.model

data class ReceiptOfGoodsItem(
    val id: Int,
    val timestamp: String,
    val synced: Boolean,
    val supplier: String,
    val warehouse: String
)