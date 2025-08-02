package com.example.loginhttp.core.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.loginhttp.core.bluetooth.ble.ConnectionEventListener
import com.example.loginhttp.core.bluetooth.ble.ConnectionManager

private const val TAG = "BluetoothViewModel"

class BluetoothViewModel(application: Application): AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _bleDevices = mutableStateListOf<DisplayBluetoothDevice>()
    val bleDevices: List<DisplayBluetoothDevice> get() = _bleDevices

    private val _isScanning = mutableStateOf(false)
    val isScanning: State<Boolean> get() = _isScanning

    private val _isConnected = mutableStateOf(false)
    val isConnected: State<Boolean> get() = _isConnected

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    private val connectionEventListener = ConnectionEventListener().apply {
        onConnectionSetupComplete = {
            Log.i(TAG, "Connected to ${it.device.address}")
            _isConnected.value = true
        }

        onDisconnect = {
            Log.i(TAG, "Disconnected from ${it.address}")
            _isConnected.value = false
            _bleDevices.clear()
        }
    }

    init {
        ConnectionManager.registerListener(connectionEventListener)
    }

    // Clear up BLE listeners when the ViewModel is cleared
    override fun onCleared() {
        ConnectionManager.unregisterListener(connectionEventListener)
        super.onCleared()
    }

    fun checkPermissions(): Boolean {
        return context.hasRequiredBluetoothPermissions()
    }

    @SuppressLint("MissingPermission")
    fun startBleScan() {
        if (!checkPermissions()) {
            Log.e(TAG, "Missing Bluetooth permissions — cannot scan")
            return
        }
        _bleDevices.clear()
        bleScanner.startScan(null, ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), scanCallback)
        _isScanning.value = true
    }

    @SuppressLint("MissingPermission")
    fun stopBleScan() {
        if (isScanning.value) {
            bleScanner.stopScan(scanCallback)
            _isScanning.value = false
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        stopBleScan()
        ConnectionManager.connect(device, context)
    }

    @SuppressLint("MissingPermission")
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val address = result.device.address
            if (_bleDevices.none { it.address == address }) {
                _bleDevices.add(
                    DisplayBluetoothDevice(
                        name = result.scanRecord?.deviceName ?: result.device.name ?: "Unnamed",
                        address = address,
                        isBle = true,
                        original = result
                    )
                )
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error code: $errorCode")
        }
    }
}