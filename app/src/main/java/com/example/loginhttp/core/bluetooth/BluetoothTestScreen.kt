package com.example.loginhttp.core.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("MissingPermission")
@Composable
fun BluetoothTestScreen(viewModel: BluetoothViewModel = viewModel()) {
    val context = LocalContext.current
    val bluetoothAdapter = remember {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    var showDevicePicker by remember { mutableStateOf(false) }
    var connectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Bluetooth is now enabled
            showDevicePicker = true
        } else {
            Toast.makeText(context, "Bluetooth not enabled.", Toast.LENGTH_SHORT).show()
        }
    }

    val launchPermissionRequest = rememberBluetoothPermissionLauncher(
        onGranted = {
            if (bluetoothAdapter?.isEnabled == true) {
                showDevicePicker = true
            } else {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        },
        onDenied = {
            Toast.makeText(context, "Permissions are required for scanning.", Toast.LENGTH_LONG).show()
        },
        onPermanentlyDenied = {
            Toast.makeText(context, "Please enable permissions in app settings.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Bluetooth Test Screen", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!context.hasRequiredBluetoothPermissions()) {
                launchPermissionRequest()
            } else if (bluetoothAdapter?.isEnabled == true) {
                showDevicePicker = true
            } else {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }) {
            Text("Scan for Devices")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isConnected.value && connectedDevice != null) {
            val name = if (context.hasRequiredBluetoothPermissions()) {
                connectedDevice?.name ?: "Unnamed"
            } else {
                "No permission"
            }
            val address = if (context.hasRequiredBluetoothPermissions()) {
                connectedDevice?.address ?: "Unknown"
            } else {
                "Unavailable"
            }

            Text("Connected to: $name ($address)", color = MaterialTheme.colorScheme.primary)
        } else {
            Text("Not connected to any device.", color = MaterialTheme.colorScheme.error)
        }
    }

    if (showDevicePicker) {
        BluetoothDevicePickerDialog(
            viewModel = viewModel,
            onDismiss = { showDevicePicker = false },
            onDeviceConnected = { device ->
                connectedDevice = device
            }
        )
    }
}