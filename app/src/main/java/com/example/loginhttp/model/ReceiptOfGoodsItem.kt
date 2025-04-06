package com.example.loginhttp.model

data class ReceiptOfGoodsItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val supplier: String,
    val warehouse: String
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}