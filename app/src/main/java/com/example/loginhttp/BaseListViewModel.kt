package com.example.loginhttp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class BaseListViewModel<T : Any> : ViewModel() {

    protected val _items = MutableStateFlow<List<T>>(emptyList())
    val items = _items.asStateFlow()

    protected val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    protected val _pendingDeleteIds = MutableStateFlow<List<Int>>(emptyList())
    val pendingDeleteIds = _pendingDeleteIds.asStateFlow()

    protected val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible = _isSheetVisible.asStateFlow()

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    fun toggleSelection(id: Int) {
        _selectedItems.value = _selectedItems.value.toMutableSet().apply {
            if (contains(id)) {
                remove(id)
            } else {
                add(id)
            }
        }
    }

    fun selectAll(ids: List<Int>) {
        _selectedItems.value = ids.toSet()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun confirmDelete(ids: List<Int>) {
        _pendingDeleteIds.value = ids
    }

    fun clearPendingDelete() {
        _pendingDeleteIds.value = emptyList()
    }

    fun executeDelete(idSelector: (T) -> Int) {
        _items.value = _items.value.filterNot { idSelector(it) in _pendingDeleteIds.value }
        _selectedItems.value -= _pendingDeleteIds.value.toSet()
        clearPendingDelete()
    }

    fun deleteSelected(idSelector: (T) -> Int) {
        executeDelete(idSelector)
    }

    fun getCurrentTimestamp(): String {
        val format = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
        return format.format(Date())
    }

    fun formatTimestamp(timestamp: Long): String {
        val format = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
        return format.format(Date(timestamp))
    }

    fun syncItem(idSelector: (T) -> Int, targetId: Int, sync: (T) -> T) {
        _items.value = _items.value.map {
            if (idSelector(it) == targetId) {
                sync(it)
            } else {
                it
            }
        }
        _selectedItems.value -= targetId
    }

    fun syncSelectedItems(idSelector: (T) -> Int, sync: (T) -> T) {
        _items.value = _items.value.map {
            if (_selectedItems.value.contains(idSelector(it))) {
                sync(it)
            } else {
                it
            }
        }
        clearSelection()
    }
}