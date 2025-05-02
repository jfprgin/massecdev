package com.example.loginhttp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.loginhttp.model.CatalogItem
import com.example.loginhttp.model.ItemType
import com.example.loginhttp.model.ManagedItem
import kotlinx.coroutines.flow.MutableStateFlow

class ItemManagementViewModel: BaseListViewModel<ManagedItem>() {

    private val allCatalogItems = listOf(
        CatalogItem(1, "Product A", "Code1", "Barcode1", "KG", ItemType.PRODUCT),
        CatalogItem(2, "Product B", "Code2", "Barcode2", "L", ItemType.PRODUCT),
        CatalogItem(3, "Product C", "Code3", "Barcode3", "M", ItemType.PRODUCT),
        CatalogItem(4, "Product D", "Code4", "Barcode4", "KG", ItemType.PRODUCT),
        CatalogItem(5, "Product E", "Code5", "Barcode5", "L", ItemType.PRODUCT),
        CatalogItem(6, "Product F", "Code6", "Barcode6", "M", ItemType.PRODUCT),
        CatalogItem(7, "Product G", "Code7", "Barcode7", "KG", ItemType.PRODUCT),
        CatalogItem(8, "Product H", "Code8", "Barcode8", "L", ItemType.PRODUCT),
        CatalogItem(9, "Product I", "Code9", "Barcode9", "M", ItemType.PRODUCT),
        CatalogItem(10, "Product J", "Code10", "Barcode10", "KG", ItemType.PRODUCT),

    )

    var inlineSearchQuery by mutableStateOf("")
        private set

    var addItemSearchQuery  by mutableStateOf("")
        private set

    var isAddItemDialogOpen  by mutableStateOf(false)
        private set

    var catalogSearchResults = MutableStateFlow<List<CatalogItem>>(emptyList())
        private set

    fun onInlineSearchChange(query: String) {
        inlineSearchQuery = query
    }

    fun onAddItemSearchChange(query: String) {
        addItemSearchQuery = query
        catalogSearchResults.value = allCatalogItems.filter { item ->
            item.name.contains(query, ignoreCase = true) ||
                    (item.code?.contains(query, ignoreCase = true) == true) ||
                    (item.barcode?.contains(query, ignoreCase = true) == true)
        }
    }

    fun openAddItemDialog() {
        isAddItemDialogOpen = true
        addItemSearchQuery = ""
        catalogSearchResults.value = allCatalogItems
    }

    fun closeAddItemDialog() {
        isAddItemDialogOpen = false
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