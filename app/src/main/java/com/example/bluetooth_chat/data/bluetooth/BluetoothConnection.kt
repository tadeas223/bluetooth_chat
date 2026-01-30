package com.example.bluetooth_chat.data.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class BluetoothConnection(
    val socket: BluetoothSocket
) : Connection {
    private val _isConnected = MutableStateFlow(socket.isConnected)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val reader = BufferedReader(
        InputStreamReader(socket.inputStream, Charsets.UTF_8)
    )
    private val writer =
        BufferedWriter(
            OutputStreamWriter(socket.outputStream, Charsets.UTF_8)
        )

    private var onDisconnect: (() -> Unit)? = null

    override suspend fun send(json: JsonObject) {
        updateConnectStatus()

        writer.write(json.toString());
        Log.d("bluetooth_chat", "sent to ${socket.remoteDevice.address} $json")
    }

    override suspend fun waitForResponse(): JsonObject {
        updateConnectStatus()

        while(true) {
            val text = reader.readLine();

            val json: JsonObject;
            try {
                json = Json.encodeToJsonElement(text).jsonObject
            } catch(_: Throwable) {
                continue;
            }

            if(json["type"] == null) {
                continue
            }

            Log.d("bluetooth_chat", "received from ${socket.remoteDevice.address} $json")
            return json;
        }
    }

    override fun onDisconnect(callback: () -> Unit) {
        onDisconnect = callback;
    }

    override suspend fun connect() {
        if(socket.isConnected) {
            updateConnectStatus()
            return
        }

        socket.connect()
        updateConnectStatus()
    }

    override fun disconnect() {
        socket.close()
        onDisconnect?.invoke()
        updateConnectStatus()
    }

    private fun updateConnectStatus() {
        if(_isConnected.value != socket.isConnected) {
            _isConnected.value = socket.isConnected
        }
    }
}