package com.example.loginhttp.model

data class IssuingGoodsItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val warehouse: String,
    val costCenter: String,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}
