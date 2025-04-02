package com.example.loginhttp

import androidx.lifecycle.ViewModel
import com.example.loginhttp.model.ReceiptOfGoodsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReceiptOfGoodsViewModel: ViewModel() {

    private val _items = MutableStateFlow<List<ReceiptOfGoodsItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible = _isSheetVisible.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    private val _pendingDeleteIds = MutableStateFlow<List<Int>>(emptyList())
    val pendingDeleteIds = _pendingDeleteIds.asStateFlow()

    // Add a new item
    fun addItem() {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = ReceiptOfGoodsItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            synced = false
        )
        _items.value += newItem
    }

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    // Timestamp helpers
    fun getCurrentTimestamp(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy. HH:mm", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date(currentTime))
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd.MM.yyyy. HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    /* =============================================================================================
     * Selection logic
     * ========================================================================================== */

    fun syncItem(id: Int) {
        _items.value = _items.value.map {
            if (it.id == id) {
                it.copy(synced = true)
            } else {
                it
            }
        }
    }

    fun toggleSelection(id: Int) {
        _selectedItems.value = if (_selectedItems.value.contains(id)) {
            _selectedItems.value - id
        } else {
            _selectedItems.value + id
        }
    }

    fun syncSelectedItems() {
        _items.value = _items.value.map {
            if (_selectedItems.value.contains(it.id)) it.copy(synced = true) else it
        }
        clearSelection()
    }

    fun selectAll(ids: List<Int>) {
        _selectedItems.value = ids.toSet()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    /* =============================================================================================
     * Delete confirmation logic
     * ========================================================================================== */

    fun confirmDelete(ids: List<Int>) {
        _pendingDeleteIds.value = emptyList()
    }

    fun clearPendingDelete() {
        _pendingDeleteIds.value = emptyList()
    }

    fun executeDelete() {
        _items.value = _items.value.filterNot {
            pendingDeleteIds.value.contains(it.id)
        }
        _selectedItems.value -= pendingDeleteIds.value.toSet()
        clearPendingDelete()
    }
}