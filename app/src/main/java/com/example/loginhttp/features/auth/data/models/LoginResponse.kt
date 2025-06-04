package com.example.loginhttp.features.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val valid: Boolean? = null,
    val counter: Int? = null,
    val companyId: Int? = null,
    val objectId: Int? = null,
    val locationId: Int? = null,
    val deviceId: String? = null,
    val deviceMac: String? = null,
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val traceId: String? = null,
)
