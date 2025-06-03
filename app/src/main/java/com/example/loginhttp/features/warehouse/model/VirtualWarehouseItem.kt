package com.example.loginhttp.features.warehouse.model

import com.example.loginhttp.model.Syncable

data class VirtualWarehouseItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val warehouse: String,
    val addedBy: String,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}