package com.example.loginhttp

import com.example.loginhttp.model.InventoryItem

class InventoryViewModel: BaseListViewModel<InventoryItem>() {

    // Add a new item to the inventory
    fun addItem(name: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = InventoryItem(
            id = newId,
            name = name,
            timestamp = getCurrentTimestamp(),
            synced = false
        )
        _items.value += newItem
    }

    fun  syncItem(id: Int) {
        syncItem(idSelector = { it.id }, targetId = id) { it.markSynced() as InventoryItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems(idSelector = { it.id}) { it.markSynced() as InventoryItem }
    }

    fun deleteSelected() {
        deleteSelected(idSelector = { it.id })
    }
}