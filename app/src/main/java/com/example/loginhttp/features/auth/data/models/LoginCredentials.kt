package com.example.loginhttp.features.auth.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_credentials")
data class LoginCredentials(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val remember: Boolean
)
