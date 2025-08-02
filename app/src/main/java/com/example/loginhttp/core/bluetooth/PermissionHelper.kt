package com.example.loginhttp.core.bluetooth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun HandleBluetoothPermissions(
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onPermanentlyDenied: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val denied = permissions.filterValues { !it }.keys

        val permanentlyDenied = denied.any { permission ->
            activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false
        }

        when {
            denied.isNotEmpty() -> onGranted()
            permanentlyDenied -> {
                showSettingsDialog = true
                onPermanentlyDenied?.invoke()
            } else -> {
                onDenied?.invoke()
                Toast.makeText(context, "Bluetooth permissions are required.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Request permissions on first composition
    LaunchedEffect(Unit) {
        val requiredPermissions = getRequiredBluetoothPermissions()
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            onGranted()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // Show fallback to app settings dialog if permissions are permanently denied
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest =  {},
            title = { Text("Permissions Required") },
            text = { Text("Bluetooth permissions were permanently denied. Please enable them in app settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${context.packageName}")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        showSettingsDialog = false
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun rememberBluetoothPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onPermanentlyDenied: (() -> Unit)? = null
): () -> Unit {
    val context = LocalContext.current
    val activity = context as? Activity

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val denied = permissions.filterValues { !it }.keys
        val permanentlyDenied = denied.any { permission ->
            activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false
        }

        when {
            denied.isEmpty() -> onGranted()
            permanentlyDenied -> onPermanentlyDenied?.invoke()
            else -> onDenied?.invoke()
        }
    }

    return {
        val permissions = getRequiredBluetoothPermissions()
        launcher.launch(permissions)
    }
}

// Utility to determine required Bluetooth permissions based on Android version
fun getRequiredBluetoothPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

fun Context.hasRequiredBluetoothPermissions(): Boolean {
    return getRequiredBluetoothPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}