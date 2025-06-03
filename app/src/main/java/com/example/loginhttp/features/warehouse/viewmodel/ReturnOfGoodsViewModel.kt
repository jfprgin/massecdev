package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.ReturnOfGoodsItem

class ReturnOfGoodsViewModel: BaseListViewModel<ReturnOfGoodsItem>() {
    fun addItem(supplier: String,warehouse: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = ReturnOfGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            supplier = supplier,
            warehouse = warehouse
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id }, id) { it.markSynced() as ReturnOfGoodsItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({ it.id }) { it.markSynced() as ReturnOfGoodsItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}