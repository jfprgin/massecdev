package com.example.loginhttp.model

data class InventoryItem(
    val id: Int,
    val name: String,
    val timestamp: String,
    override val synced: Boolean,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}
