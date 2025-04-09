package com.example.loginhttp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.LocationItem

class LocationsViewModel: BaseListViewModel<LocationItem>() {
    init {
        _items.value = listOf(
            LocationItem(1, "Šank12"),
            LocationItem(2, "Šank2"),
            LocationItem(23, "Šank1/Polica lijevo"),
            LocationItem(32, "Buldozer/Front axel")
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