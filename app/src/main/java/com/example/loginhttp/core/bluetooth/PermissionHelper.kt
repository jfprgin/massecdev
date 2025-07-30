package com.example.loginhttp.core.bluetooth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun PermissionHelper(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val permissions = remember { getBluetoothPermissions() }

    var showRationale by rememberSaveable { mutableStateOf(false) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var rationaleMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = permissions.all { result[it] == true }

        if (allGranted) {
            onPermissionGranted()
        } else {
            val permanentlyDenied = permissions.any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
            }
            if (permanentlyDenied) {
                showSettingsDialog = true
            } else {
                onPermissionDenied()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (permissionNotGranted(context, permissions)) {
            if (shouldShowRationale(context, permissions)) {
                showRationale = true
                rationaleMessage = getRationaleMessage()
            } else {
                permissionLauncher.launch(permissions)
            }
        } else {
            onPermissionGranted()
        }
    }

    if (showRationale) {
        RationaleDialog(
            message = rationaleMessage,
            onConfirm = {
                showRationale = false
                permissionLauncher.launch(permissions)
            },
            onDismiss = {
                showRationale = false
                onPermissionDenied()
            }
        )
    }

    // Permanent Denial Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            onConfirm = {
                showSettingsDialog = false
                context.openAppSettings()
            },
            onDismiss = {
                activity?.finish()
            }
        )
    }
}

@Composable
private fun RationaleDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingsDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permission Permanently Denied") },
        text = {
            Text("You've permanently denied Bluetooth permissions. Please enable them manually in app settings.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getBluetoothPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

private fun permissionNotGranted(context: Context, permissions: Array<String>) =
    permissions.any {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }

private fun shouldShowRationale(context: Context, permissions: Array<String>) =
    permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
    }

private fun getRationaleMessage(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        "This app requires Bluetooth permissions to discover and connect to nearby devices."
    } else {
        "This app requires location permission to use Bluetooth on this device."
    }
}

private fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}