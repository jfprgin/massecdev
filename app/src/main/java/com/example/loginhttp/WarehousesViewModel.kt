package com.example.loginhttp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.WarehousesItems

class WarehousesViewModel: BaseListViewModel<WarehousesItems>() {
    init {
        _items.value = listOf(
            WarehousesItems(4, "Glavno"),
            WarehousesItems(11, "Centralno"),
        )
    }

    var searchQuery by mutableStateOf("")
        private set

    var filteredItems = derivedStateOf {
        _items.value.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    fun downloadItems() {
        // Replace this with real API logic later
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}