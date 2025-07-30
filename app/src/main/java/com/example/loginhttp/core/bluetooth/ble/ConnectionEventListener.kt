package com.example.loginhttp.core.bluetooth.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

// A listener containing callback methods to be registered with [ConnectionManager].
class ConnectionEventListener {
    var onConnectionSetupComplete: ((gatt: BluetoothGatt) -> Unit)? = null

    var onDisconnect: ((device: BluetoothDevice) -> Unit)? = null

    var onCharacteristicChanged: (
        (
            device: BluetoothDevice,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) -> Unit
    )? = null

    var onCharacteristicRead: (
        (
            device:  BluetoothDevice,
            characteristic:  BluetoothGattCharacteristic,
            value:  ByteArray
        ) -> Unit
    )? = null

    var onCharacteristicWrite: (
        (
            device: BluetoothDevice,
            characteristic: BluetoothGattCharacteristic,
        ) -> Unit
    )? = null

    /* Other operations can be added here, such as:
        - onDescriptorRead
        - onDescriptorWrite
        - onNotificationsEnabled
        - onNotificationsDisabled
        - onMtuChanged
     */
}