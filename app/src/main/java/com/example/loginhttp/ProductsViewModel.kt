package com.example.loginhttp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.ProductsItem
import com.example.loginhttp.model.UnitOfMeasure

class ProductsViewModel: BaseListViewModel<ProductsItem>() {
    init {
        _items.value = listOf(
            ProductsItem(1234, 315100008, "Stvar 1", UnitOfMeasure.PIECE),
            ProductsItem(318010007, 315100009, "Stvar 2", UnitOfMeasure.KILOGRAM),
            ProductsItem(318130022, 315100010, "Stvar 3", UnitOfMeasure.LITER),
            ProductsItem(303010002, 315100011, "Stvar 4", UnitOfMeasure.NONE),
            ProductsItem(303010003, 315100012, "Stvar 5", UnitOfMeasure.NONE),
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

    fun loadItems() {
        // Replace this with real API logic later
    }

    fun exportItems() {
        // Replace this with real API logic later
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}