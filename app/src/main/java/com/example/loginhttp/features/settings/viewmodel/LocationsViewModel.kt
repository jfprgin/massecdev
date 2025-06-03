package com.example.loginhttp.features.settings.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.BaseListViewModel
import com.example.loginhttp.features.settings.model.LocationsItem

class LocationsViewModel: BaseListViewModel<LocationsItem>() {
    init {
        _items.value = listOf(
            LocationsItem(1, "Šank12"),
            LocationsItem(2, "Šank2"),
            LocationsItem(23, "Šank1/Polica lijevo"),
            LocationsItem(32, "Buldozer/Front axel")
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