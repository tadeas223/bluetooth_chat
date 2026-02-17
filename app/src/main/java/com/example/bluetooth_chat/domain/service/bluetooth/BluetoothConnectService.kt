package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface BluetoothConnectService {
    val requiredPermissions: List<String>
    val activeConnections: Flow<Map<Device, Connection>>

    val bluetoothEnabled: Flow<Boolean>

    fun requestBluetooth()
    fun onReceive(callback: ((Connection, JsonObject) -> Unit)?)
    fun startServer()
    fun stopServer()

    fun onBluetoothOff(callback: () -> Unit)

    suspend fun createConnection(address: String): Connection
}