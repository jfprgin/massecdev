package com.example.loginhttp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginhttp.data.models.LoginResponse
import com.example.loginhttp.data.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application? = null) : AndroidViewModel(application ?: Application()) {

    private val _loginState = MutableStateFlow("")
    val loginState = _loginState.asStateFlow()

    private val loginRepository = application?.let { LoginRepository(it) }

    fun loginWithCredentials(username: String, password: String, remember: Boolean) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    loginRepository?.loginWithCredentials(username, password, remember)
                }
                Log.d("LoginViewModel", "Response: $response")
                _loginState.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error: ${e.message}")
                _loginState.value = "Error: ${e.message}"
            }
        }
    }

    fun loginWithDevice(deviceId: String, deviceMac: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    loginRepository?.loginWithDevice(deviceId, deviceMac)
                }
                Log.d("LoginViewModel", "Response: $response")
                _loginState.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error: ${e.message}")
                _loginState.value = "Error: ${e.message}"
            }
        }
    }

    fun getSavedCredentials(onResult: (String, String) -> Unit) {
        viewModelScope.launch {
            val credentials = withContext(Dispatchers.IO) {
                loginRepository?.getSavedCredentials()
            }
            credentials?.let {
                onResult(it.username, it.password)
            }
        }
    }

    private fun handleResponse(response: LoginResponse?): String {
        Log.d("LoginViewModel", "Handling response: $response")
        return when {
            response?.valid == true -> {
                // Valid login response, returning success message
                "Login successful!\n$response"
            }
            response?.valid == false -> {
                // Invalid login response
                "Invalid login credentials."
            }
            response?.status == 401 -> {
                // Unauthorized (Invalid credentials)
                "Unauthorized: ${response.title}"
            }
            response?.status == 403 -> {
                // Forbidden (License expired)
                "Forbidden: ${response.title}"
            }
            else -> {
                // Catch-all for unknown responses
                "Unknown error: ${response?.type ?: "Unknown"}"
            }
        }
    }
}