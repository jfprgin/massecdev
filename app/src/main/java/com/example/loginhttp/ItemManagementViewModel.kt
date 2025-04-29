package com.example.loginhttp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.CatalogItem
import com.example.loginhttp.model.ItemType
import com.example.loginhttp.model.ManagedItem
import kotlinx.coroutines.flow.MutableStateFlow

class ItemManagementViewModel: BaseListViewModel<ManagedItem>() {

    var inlineSearchQuery by mutableStateOf("")
        private set

    var addItemSearchQuery  by mutableStateOf("")
        private set

    var isAddItemDialogOpen  by mutableStateOf(false)
        private set

    var catalogSearchresults = MutableStateFlow<List<CatalogItem>>(emptyList())
        private set

    fun onInlineSearchChange(query: String) {
        inlineSearchQuery = query
    }

    fun onAddItemSearchChange(query: String) {
        addItemSearchQuery = query
    }

    fun opeAddItemDialog() {
        isAddItemDialogOpen = true
        addItemSearchQuery = ""
        catalogSearchresults.value = emptyList()
    }

    fun closeAddItemDialog() {
        isAddItemDialogOpen = false
    }

    fun searchCatalog(query: String) {
        val fakeReuslts = listOf(
            CatalogItem(1, "Barcode1", "Code1", "Product A", "KG", ItemType.PRODUCT),
            CatalogItem(2, "Barcode2", "Code2", "Product B", "L", ItemType.PRODUCT),
            CatalogItem(3, "Barcode3", "Code3", "Product C", "KG", ItemType.PRODUCT),
        )

        catalogSearchresults.value = fakeReuslts.filter { item ->
            item.name.contains(query, ignoreCase = true) ||
            (item.code?.contains(query, ignoreCase = true) == true) ||
            (item.barcode?.contains(query, ignoreCase = true) == true)
        }
    }

    fun addItem(newItem: CatalogItem) {
        val managedItem = ManagedItem(
            id = newItem.id,
            name = newItem.name,
            type = newItem.type,
            code = newItem.code,
            unitOfMeasure = newItem.unitOfMeasure,
        )
        _items.value += managedItem
        closeAddItemDialog()
    }

    fun deleteSelected() {
        deleteSelected { it.id }
    }
}