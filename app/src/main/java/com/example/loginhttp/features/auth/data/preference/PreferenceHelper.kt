package com.example.loginhttp.features.auth.data.preference

interface PreferenceHelper {
    fun setLoggedInStatus(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun clearPreference()
}