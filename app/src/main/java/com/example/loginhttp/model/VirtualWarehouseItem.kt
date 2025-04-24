package com.example.loginhttp.model

data class VirtualWarehouseItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val warehouse: String,
    val addedBy: String,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}