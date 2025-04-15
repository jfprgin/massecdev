package com.example.loginhttp.model

data class WriteOffOfGoodsItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val warehouse: String,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}
