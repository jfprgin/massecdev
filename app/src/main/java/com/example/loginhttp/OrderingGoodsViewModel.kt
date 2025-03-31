package com.example.loginhttp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.model.OrderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderingGoodsViewModel: ViewModel() {

    private val _orders = MutableStateFlow<List<OrderItem>>(sampleOrders())
    val orders: StateFlow<List<OrderItem>> = _orders

    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems

    private val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible: StateFlow<Boolean> = _isSheetVisible

    private val _pendingDeleteIds = MutableStateFlow<List<Int>>(emptyList())
    val pendingDeleteIds: StateFlow<List<Int>> = _pendingDeleteIds

    var selectedSyncTab by mutableIntStateOf(0)   // 0 = unsynced, 1 = synced
    var selectedTypeTab by mutableIntStateOf(0)     // 0 = internal, 1 = external

    val selectedTabs = MutableStateFlow(Pair(selectedTypeTab, selectedSyncTab))

    val filteredOrders: StateFlow<List<OrderItem>> = combine(_orders, selectedTabs) { orderList, (syncTab, typeTab) ->
        val typeFiltered = orderList.filter {
            if (typeTab == 0) it.type == OrderType.INTERNAL else it.type == OrderType.EXTERNAL
        }
        typeFiltered.filter {
            if (syncTab == 0) !it.synced else it.synced
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateTabs(syncTab: Int, typeTab: Int) {
        selectedSyncTab = syncTab
        selectedTypeTab = typeTab
        selectedTabs.value = Pair(syncTab, typeTab)
    }

    // Add new order
    fun addOrder(from: String, to: String, type: OrderType) {
        val newId = (_orders.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            fromLocation = from,
            toLocation = to,
            status = "U pripremi",
            type = type,
            synced = false
        )
        _orders.value += newItem
    }

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    // Sync order
    fun syncOrder(id: Int) {
        _orders.value = _orders.value.map {
            if (it.id == id) {
                it.copy(synced = true)
            } else {
                it
            }
        }
    }

    fun syncSelectedOrders() {
        _orders.value = _orders.value.map {
            if (_selectedItems.value.contains(it.id)) {
                it.copy(synced = true)
            } else {
                it
            }
        }
    }

    // Selection logic
    fun toggleSelection(orderId: Int) {
        _selectedItems.value = if (_selectedItems.value.contains(orderId)) {
            _selectedItems.value - orderId
        } else {
            _selectedItems.value + orderId
        }
    }

    fun selectAll(ids: List<Int>) {
        _selectedItems.value = ids.toSet()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    // Delete confirmation logic
    fun confirmDelete(ids: List<Int>) {
        _pendingDeleteIds.value = ids
    }

    fun clearPendingDelete() {
        _pendingDeleteIds.value = emptyList()
    }

    fun executeDelete() {
        _orders.value = _orders.value.filterNot { pendingDeleteIds.value.contains(it.id) }
        _selectedItems.value -= pendingDeleteIds.value.toSet()
        clearPendingDelete()
    }

    // Timestamp helpers
    fun getCurrentTimestamp(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
        return dateFormat.format(Date(currentTime))
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
        return format.format(date)
    }

    private fun sampleOrders(): List<OrderItem> = listOf(
        OrderItem(1, "2025-03-28 12:00", "Skladište A", "Lokacija B", "U pripremi", OrderType.INTERNAL, false),
        OrderItem(2, "2025-03-28 13:00", "Skladište X", "Lokacija D", "Sinkronizirano", OrderType.EXTERNAL, true),
    )

    // Funkcija za prebacijvanje
}