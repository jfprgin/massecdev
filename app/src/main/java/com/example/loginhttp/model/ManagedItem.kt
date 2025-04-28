package com.example.loginhttp.model

data class ManagedItem(
    val id: Int,
    val name: String,
    val code: String?,
    val unitOfMeasure: String?,
    val type: ItemType,
)

enum class ItemType {
    PRODUCT,
    INVENTORY_LIST,
    COST_CENTER,
    OTHER,
}
