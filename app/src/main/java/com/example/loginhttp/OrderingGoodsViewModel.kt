package com.example.loginhttp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.model.OrderStatus
import com.example.loginhttp.model.OrderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val _selectedSyncTab = MutableStateFlow(OrderStatus.U_PROCESU)   // 0 = unsynced, 1 = synced
    val selectedSyncTab: StateFlow<OrderStatus> = _selectedSyncTab

    private val _selectedTypeTab = MutableStateFlow(OrderType.INTERNAL)     // 0 = internal, 1 = external
    val selectedTypeTab: StateFlow<OrderType> = _selectedTypeTab

    val filteredOrders: StateFlow<List<OrderItem>> = combine(
        _orders,
        _selectedSyncTab,
        _selectedTypeTab
    ) { orders, syncTab, typeTab ->
        orders
            .filterByType(typeTab)
            .filterBySyncStatus(syncTab)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateTabs(syncTab: OrderStatus, typeTab: OrderType) {
        _selectedSyncTab.value = syncTab
        _selectedTypeTab.value = typeTab
    }

    // Add new order
    fun addInternalOrder(fromLocation: String, toLocation: String) {
        val newId = (_orders.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem.InternalOrder(
            id = newId,
            timestamp = getCurrentTimestamp(),
            status = OrderStatus.U_PROCESU,
            fromLocation = fromLocation,
            toLocation = toLocation
        )
        _orders.value += newItem
    }

    fun addExternalOrder(supplier: String, warehouse: String) {
        val newId = (_orders.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem.ExternalOrder(
            id = newId,
            timestamp = getCurrentTimestamp(),
            status = OrderStatus.U_PROCESU,
            supplier = supplier,
            warehouse = warehouse
        )
        _orders.value += newItem
    }

    fun toggleSheet(show: Boolean) {
        _isSheetVisible.value = show
    }

    // Sync order
    fun syncOrder(id: Int) {
        _orders.value = _orders.value.map {
            if (it.id == id && it.status == OrderStatus.U_PROCESU) {
                when (it) {
                    is OrderItem.InternalOrder -> it.copy(status = OrderStatus.ZATVORENO)
                    is OrderItem.ExternalOrder -> it.copy(status = OrderStatus.ZATVORENO)
                }
            } else {
                it
            }
        }
        _selectedItems.value -= id
    }

    fun syncSelectedOrders() {
        _orders.value = _orders.value.map {
            if (_selectedItems.value.contains(it.id) && it.status == OrderStatus.U_PROCESU) {
                when (it) {
                    is OrderItem.InternalOrder -> it.copy(status = OrderStatus.ZATVORENO)
                    is OrderItem.ExternalOrder -> it.copy(status = OrderStatus.ZATVORENO)
                }
            } else {
                it
            }
        }
        clearSelection()
    }

    // Selection logic
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

    // Delete confirmation logic
    fun confirmDelete(ids: List<Int>) {
        _pendingDeleteIds.value = ids
    }

    fun clearPendingDelete() {
        _pendingDeleteIds.value = emptyList()
    }

    fun executeDelete() {
        _orders.value = _orders.value.filterNot { _pendingDeleteIds.value.contains(it.id) }
        clearSelection()
        clearPendingDelete()
    }

    // Timestamp helpers
    private fun getCurrentTimestamp(): String {
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
        OrderItem.InternalOrder(1, "28.03.2025. 12:00", OrderStatus.U_PROCESU,"Skladište A", "Lokacija B"),
        OrderItem.ExternalOrder(2, "28.03.2025. 13:00", OrderStatus.ZATVORENO, "Skladište X", "Lokacija D")
    )

    private fun List<OrderItem>.filterByType(type: OrderType): List<OrderItem> =
        when (type) {
            OrderType.INTERNAL -> this.filterIsInstance<OrderItem.InternalOrder>()
            OrderType.EXTERNAL -> this.filterIsInstance<OrderItem.ExternalOrder>()
        }

    private fun List<OrderItem>.filterBySyncStatus(status: OrderStatus): List<OrderItem> =
        filter {
            when (status) {
                OrderStatus.U_PROCESU -> it.status == OrderStatus.U_PROCESU
                OrderStatus.ZATVORENO -> it.status == OrderStatus.ZATVORENO
            }
        }

    // Funkcija za prebacijvanje

}