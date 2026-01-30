package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.Flow

interface BluetoothConnectService {
    val requiredPermissions: List<String>
    val activeConnections: Flow<Map<Device, Connection>>

    fun startServer()
    fun stopServer()

    suspend fun createConnection(address: String): Connection
}