package com.example.loginhttp.features.settings.model

data class ProductsItem(
    val barcode: Int,
    val id: Int,
    val name: String,
    val unit: UnitOfMeasure,
)

enum class UnitOfMeasure(val displayName: String) {
    LITER("L"),
    KILOGRAM("KG"),
    PIECE("KOM"),
    NONE(""),   // for blank/undefined unit
}