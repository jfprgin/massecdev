package com.example.loginhttp.model

data class CatalogItem(
    val id: Int,
    val name: String,
    val code: String?,
    val barcode: String?,
    val unitOfMeasure: String?,
    val type: ItemType,     //Defined in ManagedItem.kt
)