package com.example.loginhttp.model

data class TransferOfGoodsItem(
    val id: Int,
    val timestamp: String,
    override val synced: Boolean,
    val warehouse: String,
    val costCenter: String,
) : Syncable {
    override fun markSynced(): Syncable = copy(synced = true)
}
