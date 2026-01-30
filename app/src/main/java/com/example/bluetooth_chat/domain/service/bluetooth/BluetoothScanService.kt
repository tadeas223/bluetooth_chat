package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.StateFlow

interface BluetoothScanService {
    val devices: StateFlow<List<Device>>
    val requiredPermissions: List<String>

    fun startScan()
    fun stopScan()

}