package com.example.loginhttp

import androidx.lifecycle.ViewModel
import com.example.loginhttp.model.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderingGoodsViewModel: ViewModel() {

    private val _orderItems = MutableStateFlow<List<OrderItem>>(listOf(
        OrderItem(1, "28.03.2025. 14:12", "Skladište A", "Lokacija B", "U pripremi", "Vanjska", false),
        OrderItem(2, "28.03.2025. 13:58", "Skladište X", "Lokacija Y", "Poslano", "Interna", true),
    ))
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems

    private val _isSheetVisible = MutableStateFlow(false)
    val isSheetVisible: StateFlow<Boolean> = _isSheetVisible

    private val _pendingDeleteIds = MutableStateFlow<List<Int>>(emptyList())
    val pendingDeleteIds: StateFlow<List<Int>> = _pendingDeleteIds

    // Add new order
    fun addOrder(from: String, to: String, orderType: String) {
        val newId = (_orderItems.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem(
            id = newId,
            timestamp = getCurrentTimestamp(),
            fromLocation = from,
            toLocation = to,
            status = "U pripremi",
            orderType = orderType,
            synced = false
        )
        _orderItems.value += newItem
    }

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    // Sync order
    fun syncOrder(id: Int) {
        _orderItems.value = _orderItems.value.map {
            if (it.id == id) {
                it.copy(synced = true)
            } else {
                it
            }
        }
    }

    fun syncSelectedOrders() {
        _orderItems.value = _orderItems.value.map {
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
        _orderItems.value = _orderItems.value.filterNot { pendingDeleteIds.value.contains(it.id) }
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

    // Funkcija za prebacijvanje
}