package com.example.loginhttp.core.bluetooth.ble

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import java.util.UUID

// BluetoothGatt

fun BluetoothGatt.findCharacteristic(
    characteristicUUID: UUID,
    serviceUUID: UUID? = null,
): BluetoothGattCharacteristic? {
    return if (serviceUUID != null) {
        // If serviceUuid is available, use it to disambiguate cases where multiple services have
        // distinct characteristics that happen to use the same UUID
        services
            ?.firstOrNull { it.uuid == serviceUUID }
            ?.characteristics?.firstOrNull { it.uuid == characteristicUUID }
    } else {
        // Iterate through services and find the first one with a match for the characteristic UUID
        services.forEach { service ->
            service.characteristics?.firstOrNull { characteristic ->
                characteristic.uuid == characteristicUUID
            }?.let { matchingCharacteristic ->
                return matchingCharacteristic
            }
        }
        return null
    }
}

/*
* - printGattTable
* - findDescriptor
* */

// BluetoothGattCharacteristic

fun BluetoothGattCharacteristic.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) add("READABLE")
    if (isWritable()) add("WRITABLE")
    if (isWritableWithoutResponse()) add("WRITABLE WITHOUT RESPONSE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

/*
* - isIndicatable
* - isNotifiable
* */

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
    properties and property != 0

@SuppressLint("MissingPermission")
fun BluetoothGattCharacteristic.executeWrite(
    gatt: BluetoothGatt,
    payload: ByteArray,
    writeType: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        gatt.writeCharacteristic(this, payload, writeType)
    } else {
        // Fall back to deprecated version of writeCharacteristic for Android <13
        legacyWriteCharacteristic(gatt, payload, writeType)
    }
}

@TargetApi(Build.VERSION_CODES.S)
@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
private fun BluetoothGattCharacteristic.legacyWriteCharacteristic(
    gatt: BluetoothGatt,
    payload: ByteArray,
    writeType: Int
) {
    this.writeType = writeType
    value = payload
    gatt.writeCharacteristic(this)
}

/* BluetoothGattDescriptor
* - printProperties
* - isReadable
* - isWritable
* - containsPermission
* - executeWrite (has legacy)
* - isCccd
* */

// ByteArray
fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }
