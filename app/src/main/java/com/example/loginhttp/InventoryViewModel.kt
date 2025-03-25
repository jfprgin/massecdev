package com.example.loginhttp

import androidx.lifecycle.ViewModel
import com.example.loginhttp.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class InventoryViewModel: ViewModel() {

    private val _items = MutableStateFlow<List<InventoryItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible = _isSheetVisible.asStateFlow()

    private var idCounter = 1

    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    fun addItem(name: String) {
        val newItem = InventoryItem(
            id = idCounter++,
            name = name,
            timestamp = System.currentTimeMillis(),
            isSynced = false
        )
        _items.value += newItem
    }

    fun deleteItem(id: Int) {
        _items.value = _items.value.filter { it.id != id }
    }

    fun  syncItem(id: Int) {
        _items.value = _items.value.map {
            if (it.id == id) {
                it.copy(isSynced = true)
            } else {
                it
            }
        }
    }

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return "Created: ${format.format(date)}"
    }

    fun toggleSelection(itemId: Int) {
        _selectedItems.value = if (_selectedItems.value.contains(itemId)) {
            _selectedItems.value - itemId
        } else {
            _selectedItems.value + itemId
        }
    }

    fun syncSelectedItems() {
        _items.value = _items.value.map {
            if (_selectedItems.value.contains(it.id)) it.copy(isSynced = true) else it
        }
        clearSelection()
    }

    fun deleteSelectedItems() {
        _items.value = _items.value.filter { !selectedItems.value.contains(it.id) }
        clearSelection()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }
}