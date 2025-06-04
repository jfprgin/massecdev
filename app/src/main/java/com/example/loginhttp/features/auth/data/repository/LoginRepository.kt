package com.example.loginhttp.features.auth.data.repository

import android.content.Context
import com.example.loginhttp.features.auth.data.api.ApiService
import com.example.loginhttp.features.auth.data.database.LoginDatabase
import com.example.loginhttp.features.auth.data.models.LoginCredentials
import com.example.loginhttp.features.auth.data.models.LoginLog
import com.example.loginhttp.features.auth.data.models.LoginRequest
import com.example.loginhttp.features.auth.data.models.LoginResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val LOGIN_WITH_CREDENTIALS_URL = "http://185.203.18.87:4448/api/Login/FormLogin"
val LOGIN_WITH_DEVICE_URL = "http://185.203.18.87:4448/api/Login/DeviceLogin"
val MASSEC_DEVICE_ID = "MASSEC 1234"
val MASSEC_DEVICE_MAC = "00:06:11:22:22"

class LoginRepository(context: Context) {

    private val loginDao = LoginDatabase.getDatabase(context).loginDao()

    suspend fun loginWithCredentials(username: String, password: String, remember: Boolean): LoginResponse? {
        val requestBody = Json.encodeToString(LoginRequest(username = username, password = password))
        val response = ApiService.makePostRequest(LOGIN_WITH_CREDENTIALS_URL, requestBody)

        // Save login credentials to database if "Remember me" is checked
        if (remember && response?.valid == true) {
            saveLoginCredentials(username, password)
        }

        logLoginAttempt(username, password, MASSEC_DEVICE_ID, MASSEC_DEVICE_MAC, response)

        return response
    }

    suspend fun loginWithDevice(deviceId: String, deviceMac: String): LoginResponse? {
        val requestBody =
            Json.encodeToString(LoginRequest(deviceId = deviceId, deviceMac = deviceMac))
        val response = ApiService.makePostRequest(LOGIN_WITH_DEVICE_URL, requestBody)

        logLoginAttempt("", "", deviceId, deviceMac, response)

        return response
    }

    suspend fun getSavedCredentials(): LoginCredentials? = loginDao.getSavedCredentials()

    private suspend fun saveLoginCredentials(username: String, password: String) {
        val existingCredentials = getSavedCredentials()
        if (existingCredentials == null) {
            loginDao.insertCredentials(LoginCredentials(username = username, password = password, remember = true))
        } else {
            loginDao.updateCredentials(LoginCredentials(existingCredentials.id, username, password, true))
        }
    }

    private suspend fun logLoginAttempt(
        username: String,
        password: String,
        deviceId: String,
        deviceMac: String,
        responseJson: LoginResponse?
    ) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val response = responseJson?.let { Json.encodeToString(it) } ?: "null"

        val log = LoginLog(
            username = username,
            password = password,
            deviceId = deviceId,
            deviceMac = deviceMac,
            response = response,
            dateTime = currentTime
        )

        loginDao.insertLog(log)
    }
}