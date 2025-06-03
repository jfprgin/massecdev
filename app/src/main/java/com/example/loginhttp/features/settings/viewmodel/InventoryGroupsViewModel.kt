package com.example.loginhttp.features.settings.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.settings.model.InventoryGroupsItem

class InventoryGroupsViewModel: BaseListViewModel<InventoryGroupsItem>() {
    init {
        _items.value = listOf(
            InventoryGroupsItem(1, "Roto Dinamic d.o.o."),
            InventoryGroupsItem(2, "CEDEVITA"),
            InventoryGroupsItem(3, "Lobby"),
            InventoryGroupsItem(4, "Glavno skladište"),
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

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}