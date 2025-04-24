package com.example.loginhttp

import com.example.loginhttp.model.VirtualWarehouseItem

class VirtualWarehouseViewModel: BaseListViewModel<VirtualWarehouseItem>() {

    fun addItem(warehouse: String, addedBy: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = VirtualWarehouseItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            warehouse = warehouse,
            addedBy = addedBy
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id }, id) { it.markSynced() as VirtualWarehouseItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({ it.id }) { it.markSynced() as VirtualWarehouseItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}