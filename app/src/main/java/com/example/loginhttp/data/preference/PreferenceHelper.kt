package com.example.loginhttp.data.preference

interface PreferenceHelper {
    fun setLoggedInStatus(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun clearPreference()
}