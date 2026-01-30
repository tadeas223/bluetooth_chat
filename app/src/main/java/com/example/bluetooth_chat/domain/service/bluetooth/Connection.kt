package com.example.bluetooth_chat.domain.service.bluetooth

import com.example.bluetooth_chat.domain.model.bluetooth.Device
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonObject

interface Connection {
    val isConnected: StateFlow<Boolean>

    suspend fun send(json: JsonObject)

    suspend fun waitForResponse(): JsonObject

    fun onDisconnect(callback: () -> Unit)

    suspend fun connect();
    fun disconnect();
}