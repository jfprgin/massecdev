package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.IssuingGoodsItem

class IssuingGoodsViewModel: BaseListViewModel<IssuingGoodsItem>() {

    fun addItem(warehouse: String, costCenter: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = IssuingGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false,
            warehouse = warehouse,
            costCenter = costCenter
        )
        _items.value += newItem
    }

    fun syncItem(id: Int) {
        syncItem({ it.id }, id) { it.markSynced() as IssuingGoodsItem }
    }

    fun syncSelectedItems() {
        syncSelectedItems({ it.id }) { it.markSynced() as IssuingGoodsItem }
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}