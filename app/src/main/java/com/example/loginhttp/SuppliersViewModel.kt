package com.example.loginhttp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.SuppliersItem

class SuppliersViewModel: BaseListViewModel<SuppliersItem>() {
    init {
        _items.value = listOf(
            SuppliersItem(123, "Roto Dinamic d.o.o."),
            SuppliersItem(2, "Korisnik inicijalni"),
            SuppliersItem(3, "Tajna tvrtka")
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