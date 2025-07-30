package com.example.loginhttp.core.bluetooth.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import java.util.UUID

// Abstract sealed class representing different BLE operations
sealed class BleOperationType {
    abstract val device: BluetoothDevice
}

// Connect to [device] and perform service discovery
data class Connect(
    override val device: BluetoothDevice,
    val context: Context
) : BleOperationType()

// Disconnect from [device] and release all connection resources
data class Disconnect(
    override val device: BluetoothDevice
) : BleOperationType()

// Write [payload] as the value of a characteristic represented by [characteristicUuid]
data class CharacteristicWrite(
    override val device: BluetoothDevice,
    val characteristicUUID: UUID,
    val writeType: Int,
    val payload: ByteArray
) : BleOperationType() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacteristicWrite

        if (device != other.device) return false
        if (characteristicUUID != other.characteristicUUID) return false
        if (writeType != other.writeType) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + characteristicUUID.hashCode()
        result = 31 * result + writeType
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

// Read the value of a characteristic represented by [characteristicUuid]
data class CharacteristicRead(
    override val device: BluetoothDevice,
    val characteristicUUID: UUID
) : BleOperationType()

/* Other operations can be added here, such as:
   - DescriptorRead
   - DescriptorWrite
   - MtuRequest
   - EnableNotifications
   - DisableNotifications
*/