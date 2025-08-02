package com.example.loginhttp.core.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class DisplayBluetoothDevice(
    val name: String?,
    val address: String,
    val isBle: Boolean,
    val original: Any // Holds either BluetoothDevice or ScanResult
)

@Composable
fun BluetoothDevicePickerDialog(
    viewModel: BluetoothViewModel,
    onDismiss: () -> Unit,
    onDeviceConnected: (BluetoothDevice) -> Unit
) {
    LaunchedEffect(true) {
        viewModel.startBleScan()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopBleScan()
        }
    }

    ScanResultPicerDialog(
        bleDevices = viewModel.bleDevices,
        classicDevices = emptyList(),
        onDismiss = onDismiss,
        onDeviceSelected = {
            val device = (it.original as? ScanResult)?.device ?: return@ScanResultPicerDialog
            viewModel.connectToDevice(device)
            onDeviceConnected(device)
            onDismiss()
        }
    )
}

@Composable
fun ScanResultPicerDialog(
    bleDevices: List<DisplayBluetoothDevice>,
    classicDevices: List<DisplayBluetoothDevice>,
    onDismiss: () -> Unit,
    onDeviceSelected: (DisplayBluetoothDevice) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("BLE Devices", "Classic Devices")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Select a Bluetooth Device")
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                        )
                    }
                }
            }
        },
        text = {
            val currentList = if (selectedTab == 0) bleDevices else classicDevices

            if (currentList.isEmpty()) {
                Text("No devices found in this category.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(currentList) { device ->
                        BluetoothDeviceRow(device, onDeviceSelected)
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }

    )
}

@Composable
fun BluetoothDeviceRow(
    device: DisplayBluetoothDevice,
    onClick: (DisplayBluetoothDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(12.dp)
    ) {
        Text(text = device.name ?: "Unnamed Device", style = MaterialTheme.typography.bodyLarge)
        Text(text = device.address, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun ScanResultPickerDialogPreview() {
    val bleDevices = listOf(
        DisplayBluetoothDevice("BLE Device 1", "00:11:22:33:44:55", true, Any()),
        DisplayBluetoothDevice("BLE Device 2", "66:77:88:99:AA:BB", true, Any())
    )

    val classicDevices = listOf(
        DisplayBluetoothDevice("Classic Device 1", "CC:DD:EE:FF:00:11", false, Any()),
        DisplayBluetoothDevice("Classic Device 2", "22:33:44:55:66:77", false, Any())
    )

    ScanResultPicerDialog(
        bleDevices = bleDevices,
        classicDevices = classicDevices,
        onDismiss = {},
        onDeviceSelected = {}
    )
}