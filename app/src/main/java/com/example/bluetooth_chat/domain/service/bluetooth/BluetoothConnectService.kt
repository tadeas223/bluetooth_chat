package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface BluetoothConnectService {
    val requiredPermissions: List<String>
    val activeConnections: Flow<Map<Device, Connection>>

    fun onReceive(callback: ((Connection, JsonObject) -> Unit)?)
    fun startServer()
    fun stopServer()

    suspend fun createConnection(address: String): Connection
}