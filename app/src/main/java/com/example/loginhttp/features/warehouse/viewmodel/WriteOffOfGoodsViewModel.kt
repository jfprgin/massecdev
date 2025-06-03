package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.WriteOffOfGoodsItem

class WriteOffOfGoodsViewModel: BaseListViewModel<WriteOffOfGoodsItem>() {

    fun addItem(warehouse: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = WriteOffOfGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            warehouse = warehouse
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id }, id) { it.markSynced() as WriteOffOfGoodsItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({ it.id }) { it.markSynced() as WriteOffOfGoodsItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}