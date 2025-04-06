package com.example.loginhttp

import androidx.lifecycle.viewModelScope
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.model.OrderStatus
import com.example.loginhttp.model.OrderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OrderingGoodsViewModel: BaseListViewModel<OrderItem>() {

    private val _selectedSyncTab = MutableStateFlow(OrderStatus.U_PROCESU)   // 0 = unsynced, 1 = synced
    val selectedSyncTab: StateFlow<OrderStatus> = _selectedSyncTab

    private val _selectedTypeTab = MutableStateFlow(OrderType.INTERNAL)     // 0 = internal, 1 = external
    val selectedTypeTab: StateFlow<OrderType> = _selectedTypeTab

    val syncedCount: StateFlow<Int> = combine(items, selectedSyncTab, selectedTypeTab) { list, _, typeTab ->
        list.filterByType(typeTab)
            .count { it.status == OrderStatus.ZATVORENO }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val unsyncedCount: StateFlow<Int> = combine(items, selectedSyncTab, selectedTypeTab) { list, _, typeTab ->
        list.filterByType(typeTab)
            .count { it.status == OrderStatus.U_PROCESU }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        _items.value = sampleOrders()
    }

    val filteredOrders: StateFlow<List<OrderItem>> = combine(
        _items,
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
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem.InternalOrder(
            id = newId,
            timestamp = getCurrentTimestamp(),
            status = OrderStatus.U_PROCESU,
            fromLocation = fromLocation,
            toLocation = toLocation
        )
        _items.value += newItem
    }

    fun addExternalOrder(supplier: String, warehouse: String) {
        val newId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = OrderItem.ExternalOrder(
            id = newId,
            timestamp = getCurrentTimestamp(),
            status = OrderStatus.U_PROCESU,
            supplier = supplier,
            warehouse = warehouse
        )
        _items.value += newItem
    }

    // Sync order
    fun syncOrder(id: Int) {
        _items.value = _items.value.map {
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
        _items.value = _items.value.map {
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

    fun deleteSelected() {
        deleteSelected { it.id }
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