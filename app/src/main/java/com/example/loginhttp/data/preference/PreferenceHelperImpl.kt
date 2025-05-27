package com.example.loginhttp.data.preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelperImpl(context: Context): PreferenceHelper {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun setLoggedInStatus(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    override fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    override fun clearPreference() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "app_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}