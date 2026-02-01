package com.example.bluetooth_chat.data.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.example.bluetooth_chat.data.bluetooth.FoundDeviceReceiver
import com.example.bluetooth_chat.data.bluetooth.toDevice
import com.example.bluetooth_chat.data.hasPermissions
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothScanService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.collections.plus

class AndroidBluetoothScanService @Inject constructor(
    @ApplicationContext private val context: Context
): BluetoothScanService {
    override val requiredPermissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )

    private var isScanning = false

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _devices.update { devices ->
            val newDevice = device.toDevice()
            if (newDevice in devices) {
                Log.d(
                    "Bluetooth_chat",
                    "again scanned device: [${newDevice.address}] ${newDevice.name}"
                )
                devices
            } else {
                Log.d("Bluetooth_chat", "scanned device: [${newDevice.address}] ${newDevice.name}")
                devices + newDevice
            }
        }
    }

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    override val devices: StateFlow<List<Device>>
        get() = _devices.asStateFlow()

    @SuppressLint("MissingPermission")
    override fun startScan() {
        if(!hasPermissions(context, requiredPermissions)) {
            throw SecurityException("missing required permissions")
        }

        if(isScanning) {
            return
        } else {
            isScanning = true
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        Log.d("Bluetooth_chat", "BluetoothAdapter enabled: ${bluetoothAdapter?.isEnabled}")
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        if(!hasPermissions(context, requiredPermissions)) {
            throw SecurityException("missing required permissions")
        }

        if(!isScanning) {
            return
        } else {
            isScanning = false
        }

        _devices.value = emptyList()

        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(foundDeviceReceiver)
    }
}