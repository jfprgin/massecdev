package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.ReceiptOfGoodsItem

class ReceiptOfGoodsViewModel: BaseListViewModel<ReceiptOfGoodsItem>() {

    // Add a new item
    fun addItem(supplier: String, warehouse: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = ReceiptOfGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            supplier = supplier,
            warehouse = warehouse
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id}, id) { it.markSynced() as ReceiptOfGoodsItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({it.id}) { it.markSynced() as ReceiptOfGoodsItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}