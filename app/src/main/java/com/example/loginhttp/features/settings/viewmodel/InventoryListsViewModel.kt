package com.example.loginhttp.features.settings.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.settings.model.InventoryListsItem

class InventoryListsViewModel: BaseListViewModel<InventoryListsItem>() {
    init {
        _items.value = listOf(
            InventoryListsItem(1, "Lista 1"),
            InventoryListsItem(2, "Pivo"),
            InventoryListsItem(3, "Sokovi"),
            InventoryListsItem(4, "Lobby"),
            InventoryListsItem(5, "Ostalo"),
        )
    }

    var searchQuery by mutableStateOf("")
        private set

    val filteredItems = derivedStateOf {
        _items.value.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    fun downloadItems() {
        // Replace this with real API logic later
    }

    fun changeItemVisibility(item: InventoryListsItem) {
        _items.value = _items.value.map {
            if (it.id == item.id) {
                it.copy(isVisible = !it.isVisible)
            } else {
                it
            }
        }
    }

    fun changeSelectedItemVisibility() {
        _items.value = _items.value.map {
            if (_selectedItems.value.contains(it.id)) {
                it.copy(isVisible = !it.isVisible)
            } else {
                it
            }
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}