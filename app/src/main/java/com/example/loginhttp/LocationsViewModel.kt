package com.example.loginhttp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.loginhttp.model.LocationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNot

class LocationsViewModel: ViewModel() {
    private val _locations = MutableStateFlow(
        listOf(
            LocationItem(1, "Šank12"),
            LocationItem(2, "Šank2"),
            LocationItem(23, "Šank1/Polica lijevo"),
            LocationItem(32, "Buldozer/Front axel")
        )
    )
    val locations = _locations.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    private val _pendingDeleteIds = MutableStateFlow<List<Int>>(emptyList())
    val pendingDeleteIds = _pendingDeleteIds.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    val filteredLocations = derivedStateOf {
        _locations.value.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    /* =============================================================================================
     * Selection logic
     * ========================================================================================== */
    fun toggleSelection(id: Int) {
        _selectedItems.value = _selectedItems.value.toMutableSet().also {
            if (it.contains(id)) it.remove(id) else it.add(id)
        }
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun selectAll(ids: List<Int>) {
        _selectedItems.value = ids.toSet()
    }

    /* =============================================================================================
     * Delete confirmation logic
     * ========================================================================================== */

    fun confirmDelete(ids: List<Int>) {
        _pendingDeleteIds.value = ids
    }

    fun clearPendingDelete() {
        _pendingDeleteIds.value = emptyList()
    }

    fun executeDelete() {
        _locations.value = _locations.value.filterNot { pendingDeleteIds.value.contains(it.id) }
        _selectedItems.value = _selectedItems.value - pendingDeleteIds.value.toSet()
        clearPendingDelete()
    }

    /* =============================================================================================
     * Sync logic
     * ========================================================================================== */
    fun downloadLocations() {
        // Download locations from the server
    }
}