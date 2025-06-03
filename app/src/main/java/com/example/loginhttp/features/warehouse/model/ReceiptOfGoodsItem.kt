package com.example.loginhttp.features.warehouse.model

import com.example.loginhttp.model.Syncable

data class ReceiptOfGoodsItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val supplier: String,
    val warehouse: String
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}