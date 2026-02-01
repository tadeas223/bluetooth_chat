package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonObject

interface Connection {
    val isConnected: StateFlow<Boolean>

    val name: String?
    val address: String

    suspend fun send(json: JsonObject)

    suspend fun waitForResponse(requestId: String): JsonObject

    fun onReceive(callback: ((Connection, JsonObject) -> Unit)?)

    fun onDisconnect(callback: (() -> Unit)?)

    suspend fun connect();
    fun disconnect();
}