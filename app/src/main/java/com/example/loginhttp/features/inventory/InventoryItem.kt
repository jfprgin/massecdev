package com.example.loginhttp.features.inventory

import com.example.loginhttp.model.Syncable

data class InventoryItem(
    val id: Int,
    val name: String,
    val timestamp: String,
    override val synced: Boolean,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}
