package com.example.loginhttp.features.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String? = null,
    val password: String? = null,
    val deviceId: String? = null,
    val deviceMac: String? = null,
)
