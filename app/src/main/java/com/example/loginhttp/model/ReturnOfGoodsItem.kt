package com.example.loginhttp.model

data class ReturnOfGoodsItem(
    val id: Int,
    val timestamp: String,
    val synced: Boolean,
    val supplier: String,
    val warehouse: String
) {
    fun markSynced(): ReturnOfGoodsItem {
        return this.copy(synced = true)
    }
}