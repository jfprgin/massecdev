package com.example.loginhttp.features.warehouse.viewmodel

import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.warehouse.model.TemplatesItem

class TemplatesViewModel: BaseListViewModel<TemplatesItem>() {

    fun addItem(name: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = TemplatesItem(
            id = newId,
            name = name
        )
        _items.value += newItem
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}