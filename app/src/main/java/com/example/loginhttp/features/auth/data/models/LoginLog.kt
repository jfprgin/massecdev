package com.example.loginhttp.features.auth.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_logs")
data class LoginLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val deviceId: String,
    val deviceMac: String,
    val response: String,
    val dateTime: String
)
