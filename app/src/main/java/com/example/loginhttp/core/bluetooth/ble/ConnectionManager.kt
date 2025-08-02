package com.example.loginhttp.core.bluetooth.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

// Maximum and minimum MTU sizes for GATT connections
//private const val GATT_MAX_MTU_SIZE = 517
//private const val GATT_MIN_MTU_SIZE = 23

private const val TAG = "ConnectionManager"

@Suppress("MissingPermission")  // Assume permissions are handled by UI
object ConnectionManager {
    private var listeners: MutableSet<WeakReference<ConnectionEventListener>> = mutableSetOf()
    private val listenersAsSet
        get() = listeners.toSet()

    private val deviceGattMap = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()
    private val operationQueue = ConcurrentLinkedQueue<BleOperationType>()
    private var pendingOperation: BleOperationType? = null

    fun servicesOnDevice(device: BluetoothDevice): List<BluetoothGattService>? =
        deviceGattMap[device]?.services

    // listenToBondStateChanges

    fun registerListener(listener: ConnectionEventListener) {
        if (listeners.map { it.get() }.contains(listener)) { return }
        listeners.add(WeakReference(listener))
        listeners = listeners.filter { it.get() != null }.toMutableSet()
        Log.d(TAG, "Added listener $listener, ${listeners.size} listeners total")
    }

    fun unregisterListener(listener: ConnectionEventListener) {
        // Removing elements while in a loop results in a java.util.ConcurrentModificationException
        var toRemove: WeakReference<ConnectionEventListener>? = null
        listeners.forEach {
            if (it.get() == listener) {
                toRemove = it
            }
        }
        toRemove?.let {
            listeners.remove(it)
            Log.d(TAG, "Removed listener ${it.get()}, ${listeners.size} listeners total")
        }
    }

    fun connect(device: BluetoothDevice, context: Context) {
        if (device.isConnected()) {
            Log.e(TAG, "Device ${device.name} (${device.address}) is already connected")
        } else {
            enqueueOperation(Connect(device, context.applicationContext))
        }
    }

    fun teardownConnection(device: BluetoothDevice) {
        if (device.isConnected()) {
            enqueueOperation(Disconnect(device))
        } else {
            Log.e(TAG, "Not connected to ${device.name} (${device.address}), cannot teardown connection!")
        }
    }

    fun readCharacteristic(
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic
    ) {
        if (device.isConnected() && characteristic.isReadable()) {
            enqueueOperation(CharacteristicRead(device, characteristic.uuid))
        } else if (!characteristic.isReadable()) {
            Log.e(TAG, "Attempting to read ${characteristic.uuid} that isn't readable!")
        } else if (!device.isConnected()) {
            Log.e(TAG, "Not connected to ${device.name} (${device.address}), cannot read characteristic!")
        }
    }

    fun writeCharacteristic(
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic,
        payload: ByteArray,
    ) {
        val writeType = when {
            characteristic.isReadable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> {
                Log.e(TAG, "Attempting to write to ${characteristic.uuid} that isn't writable!")
                return
            }
        }
        if (device.isConnected()) {
            enqueueOperation(
                CharacteristicWrite(device, characteristic.uuid, writeType, payload)
            )
        } else {
            Log.e(TAG, "Not connected to ${device.name} (${device.address}), cannot perform characteristic write")
        }
    }

    /* Other operations can be added here, such as:
        - readDescriptor
        - writeDescriptor
        - enableNotifications
        - disableNotifications
        - requestMtu
     */

    @Synchronized
    private fun enqueueOperation(operation: BleOperationType) {
        operationQueue.add(operation)
        if (pendingOperation == null) {
            doNextOperation()
        }
    }

    @Synchronized
    private fun signalEndOfOperation() {
        Log.d(TAG, "End of operation: $pendingOperation")
        pendingOperation = null
        if (operationQueue.isNotEmpty()) {
            doNextOperation()
        }
    }

    // Perform a given [BleOperationType]. All permission checks are performed
    // before an operation can be enqueued by [enqueueOperation].
    @Synchronized
    private fun doNextOperation() {
        if (pendingOperation != null) {
            Log.e(TAG, "doNextOperation() called when an operation is pending! Aborting.")
            return
        }

        val operation = operationQueue.poll() ?: run {
            Log.v(TAG, "Operation queue empty, returning")
            return// Handle Connect separately from other operations that require device to be connected
        }
        pendingOperation = operation

        // Handle Connect separately from other operations that require device to be connected
        if (operation is Connect) {
            with(operation) {
                Log.w(TAG, "Connecting to device ${device.name} (${device.address})")
                device.connectGatt(context, false, callback)
            }
            return
        }

        // Check BluetoothGatt availability for other operations
        val gatt = deviceGattMap[operation.device]
            ?: this@ConnectionManager.run {
                Log.e(TAG, "Not connected to ${operation.device.address}! Aborting $operation operation.")
                signalEndOfOperation()
                return
            }

        when (operation) {
            is Disconnect -> with(operation) {
                Log.w(TAG, "Disconnecting from device ${device.name} (${device.address})")
                gatt.close()
                deviceGattMap.remove(device)
                listenersAsSet.forEach { it.get()?.onDisconnect?.invoke(device) }
                signalEndOfOperation()
            }
            is CharacteristicWrite -> with(operation) {
                gatt.findCharacteristic(characteristicUUID)?.executeWrite(
                    gatt,
                    payload,
                    writeType
                ) ?: this@ConnectionManager.run {
                    Log.e(TAG, "Cannot find $characteristicUUID to write to")
                    signalEndOfOperation()
                }
            }
            is CharacteristicRead -> with(operation) {
                gatt.findCharacteristic(characteristicUUID)?.let { characteristic ->
                    gatt.readCharacteristic(characteristic)
                } ?: this@ConnectionManager.run {
                    Log.e(TAG, "Cannot find $characteristicUUID to read from")
                    signalEndOfOperation()
                }
            }
            /* Other operations can be added here, such as:
                - DescriptorRead
                - DescriptorWrite
                - EnableNotifications
                - DisableNotifications
                - MtuRequest
            */
           else -> error("Unsupported operation: $operation")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private val callback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceName = gatt.device.name
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w(TAG, "onConnectionStateChange: connected to $deviceName ($deviceAddress)")
                    deviceGattMap[gatt.device] = gatt
                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e(TAG, "onConnectionStateChange: disconnected from $deviceName ($deviceAddress)")
                    teardownConnection(gatt.device)
                }
            } else {
                Log.e(TAG, "onConnectionStateShange: status $status encountered for $deviceName ($deviceAddress)")
                if (pendingOperation is Connect) {
                    signalEndOfOperation()
                }
                teardownConnection(gatt.device)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.w(TAG, "Discovered ${services.size} services for ${device.address}")
                    // printGattTable()
                    // requestMtu(device, GATT_MAX_MTU_SIZE)
                    listenersAsSet.forEach { it.get()?.onConnectionSetupComplete?.invoke(this) }
                } else {
                    Log.e(TAG, "Service discovery failed due to status $status")
                    teardownConnection(gatt.device)
                }
            }

            if (pendingOperation is Connect) {
                signalEndOfOperation()
            }
        }

        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(TAG, "Read characteristic $uuid | value: ${value.toHexString()}")
                        listenersAsSet.forEach {
                            it.get()?.onCharacteristicRead?.invoke(
                                gatt.device,
                                this,
                                value
                            )
                        }
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e(TAG, "Read not permitted for $uuid")
                    }
                    else -> {
                        Log.e(TAG, "Characteristic read failed for $uuid, error: $status")
                    }
                }
            }

            if (pendingOperation is CharacteristicRead) {
                signalEndOfOperation()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            val uuid = characteristic.uuid
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i(TAG, "Read characteristic $uuid | value: ${value.toHexString()}")
                    listenersAsSet.forEach {
                        it.get()?.onCharacteristicRead?.invoke(
                            gatt.device,
                            characteristic,
                            value
                        )
                    }
                }
                BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                    Log.e(TAG, "Read not permitted for $uuid")
                }
                else -> {
                    Log.e(TAG, "Characteristic read failed for $uuid, error: $status")
                }
            }

            if (pendingOperation is CharacteristicRead) {
                signalEndOfOperation()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            val writtenValue = (pendingOperation as? CharacteristicWrite)?.payload
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(TAG, "Wrote to characteristic $uuid | value: ${writtenValue?.toHexString()}")
                        listenersAsSet.forEach {
                            it.get()?.onCharacteristicWrite?.invoke(gatt.device, this)
                        }
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Log.e(TAG, "Write not permitted for $uuid")
                    }
                    else -> {
                        Log.e(TAG, "Characteristic write failed for $uuid, error: $status")
                    }
                }
            }
            if (pendingOperation is CharacteristicWrite) {
                signalEndOfOperation()
            }
        }

        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                Log.i(TAG, "Characteristic $uuid changed | value: ${value.toHexString()}")
                listenersAsSet.forEach {
                    it.get()?.onCharacteristicChanged?.invoke(gatt.device, this, value)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.i(TAG, "Characteristic ${characteristic.uuid} changed | value: ${value.toHexString()}")
            listenersAsSet.forEach {
                it.get()?.onCharacteristicChanged?.invoke(gatt.device, characteristic, value)
            }
        }

        /* Other operations can be added here, such as:
            - onMtuChanged
            - onDescriptorRead
            - onDescriptorWrite
            -  onCccdWrite
        */
    }

    private val broadcastReceiver = object  : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            with(intent) {
                if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    val device = parcelableExtraCompat<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val previousBondState = getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)
                    val bondState = getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    val bondTransition = "${previousBondState.toBondStateDescription()} to " +
                            bondState.toBondStateDescription()
                    Log.w(TAG, "${device?.address} bond state changed | $bondTransition")
                }
            }
        }

        private fun Int.toBondStateDescription() = when (this) {
            BluetoothDevice.BOND_BONDED -> "BONDED"
            BluetoothDevice.BOND_BONDING -> "BONDING"
            BluetoothDevice.BOND_NONE -> "NOT BONDED"
            else -> "ERROR: $this"
        }
    }

    private fun BluetoothDevice.isConnected() = deviceGattMap.containsKey(this)

    // A backwards compatible approach of obtaining a parcelable extra from an [Intent] object.
    internal inline fun <reified  T : Parcelable> Intent.parcelableExtraCompat(key: String): T? = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}

