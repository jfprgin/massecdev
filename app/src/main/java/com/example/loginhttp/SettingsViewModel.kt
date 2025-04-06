package com.example.loginhttp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel(application: Application? = null) : AndroidViewModel(application ?: Application()) {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _lastSyncedTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncedTime = _lastSyncedTime.asStateFlow()

    fun refreshDatabase() {
        viewModelScope.launch {
            _isRefreshing.value = true

            // Call your database sync / refresh logic
            // For demonstration, fake delay
            delay(2000)

            _lastSyncedTime.value = System.currentTimeMillis()
            _isRefreshing.value = false
        }
    }

    fun getLastSyncedText(): String {
        val diffMilis = System.currentTimeMillis() - _lastSyncedTime.value
        val minutes = TimeUnit.MICROSECONDS.toMinutes(diffMilis)
        return if (minutes < 1) {
            "Last synced: Just now"
        } else {
            "Last synced: $minutes minute${if (minutes > 1) "s" else ""} ago"
        }
    }
}