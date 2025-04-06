package com.example.loginhttp.model

interface Syncable {
    val synced: Boolean
    fun markSynced(): Syncable
}