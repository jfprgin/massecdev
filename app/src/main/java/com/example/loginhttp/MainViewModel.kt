package com.example.loginhttp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.loginhttp.features.auth.data.preference.PreferenceHelperImpl

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelperImpl(getApplication())

    fun isAuthenticated(): Boolean {
        return preferenceHelper.isLoggedIn()
    }

    fun logout() {
        preferenceHelper.setLoggedInStatus(false)
    }
}