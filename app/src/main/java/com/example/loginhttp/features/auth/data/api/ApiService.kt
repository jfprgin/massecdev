package com.example.loginhttp.features.auth.data.api

import android.util.Log
import com.example.loginhttp.features.auth.data.models.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

object ApiService {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun makePostRequest(urlString: String, jsonBody: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                connection.outputStream.use { os ->
                    os.write(jsonBody.toByteArray())
                    os.flush()
                }

                val responseCode = connection.responseCode
                val responseStream =
                    if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val responseText = responseStream.bufferedReader().use { it.readText() }

                Log.d("ApiService", "Response Code: $responseCode")
                Log.d("ApiService", "Response Text: $responseText")

                return@withContext try {
                    json.decodeFromString<LoginResponse>(responseText)
                } catch (e: Exception) {
                    Log.e("ApiService", "Failed to parse response: ${e.message}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                connection.disconnect()
            }
        }
    }
}