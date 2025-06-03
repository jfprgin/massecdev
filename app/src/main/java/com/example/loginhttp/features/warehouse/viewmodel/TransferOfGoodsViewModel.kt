package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.TransferOfGoodsItem

class TransferOfGoodsViewModel: BaseListViewModel<TransferOfGoodsItem>() {

    fun addItem(warehouse: String, costCenter: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = TransferOfGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            warehouse = warehouse,
            costCenter = costCenter
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id }, id) { it.markSynced() as TransferOfGoodsItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({ it.id }) { it.markSynced() as TransferOfGoodsItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}