package com.example.loginhttp.core.bluetooth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionTestScreen() {
    var permissionState by remember { mutableStateOf("Waiting for response...") }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Bluetooth Permission Test") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Permission Status: $permissionState")
        }

        // 🔧 Inject your permission logic here
        PermissionHelper(
            onPermissionGranted = {
                permissionState = "✅ Permission Granted"
            },
            onPermissionDenied = {
                permissionState = "❌ Permission Denied (or Cancelled)"
            }
        )
    }
}