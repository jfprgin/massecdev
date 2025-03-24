package com.example.loginhttp

import androidx.lifecycle.ViewModel
import com.example.loginhttp.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class InventoryViewModel: ViewModel() {

    private val _items = MutableStateFlow<List<InventoryItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible = _isSheetVisible.asStateFlow()

    private var idCounter = 1

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

    fun timeAgo(timeStamp: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis()) - timeStamp)
        return if (minutes < 1) "Just now" else "$minutes minutes ago"
    }
}