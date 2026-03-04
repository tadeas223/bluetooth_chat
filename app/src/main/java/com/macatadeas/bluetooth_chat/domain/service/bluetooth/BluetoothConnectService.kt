package com.macatadeas.bluetooth_chat.domain.service.bluetooth

import com.macatadeas.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject

interface BluetoothConnectService {
    val requiredPermissions: List<String>
    val activeConnections: Flow<Map<Device, Connection>>

    val incomingPackets: SharedFlow<Pair<Connection, JsonObject>>
    val bluetoothEnabled: Flow<Boolean>

    fun requestBluetooth()
    fun startServer()
    fun stopServer()


    suspend fun createConnection(address: String): Connection
}