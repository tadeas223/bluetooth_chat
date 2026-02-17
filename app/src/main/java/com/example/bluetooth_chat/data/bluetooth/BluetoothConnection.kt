package com.example.bluetooth_chat.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.coroutines.resume

class BluetoothConnection(
    private val socket: BluetoothSocket
) : Connection {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isConnected = MutableStateFlow(socket.isConnected)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    @SuppressLint("MissingPermission")
    override val name: String? = socket.remoteDevice.name
    override val address: String = socket.remoteDevice.address

    private val writer = BufferedWriter(
        OutputStreamWriter(socket.outputStream, Charsets.UTF_8)
    )

    private val reader = BufferedReader(
        InputStreamReader(socket.inputStream, Charsets.UTF_8)
    )

    private var onDisconnect: (() -> Unit)? = null
    private var onReceive: ((Connection, JsonObject) -> Unit)? = null

    private val _incoming = MutableSharedFlow<JsonObject>(
        extraBufferCapacity = 64
    )
    val incoming: SharedFlow<JsonObject> = _incoming.asSharedFlow()

    private val pending = mutableMapOf<String, CancellableContinuation<JsonObject>>()
    private val pendingMutex = Mutex()

    override suspend fun connect() {
        if (!socket.isConnected) {
            socket.connect()
        }

        updateConnectStatus()
        startReaderLoop()
    }

    override fun disconnect() {
        scope.cancel()
        try {
            socket.close()
        } catch (_: Throwable) {}

        updateConnectStatus()
        onDisconnect?.invoke()
    }

    override suspend fun send(json: JsonObject) {
        updateConnectStatus()

        writer.write(json.toString())
        writer.newLine()
        writer.flush()

        Log.d("Bluetooth_chat", "sent to $address $json")
    }

    override suspend fun waitForResponse(requestId: String): JsonObject =
        suspendCancellableCoroutine { cont ->
            scope.launch {
                pendingMutex.withLock {
                    pending[requestId] = cont
                }
            }

            cont.invokeOnCancellation {
                scope.launch {
                    pendingMutex.withLock {
                        pending.remove(requestId)
                    }
                }
            }
        }

    override fun onDisconnect(callback: (() -> Unit)?) {
        onDisconnect = callback
    }

    override fun onReceive(callback: ((Connection, JsonObject) -> Unit)?) {
        onReceive = callback
    }

    private fun startReaderLoop() {
        scope.launch {
            Log.d("Bluetooth_chat", "Listening on $address")

            try {
                while (isActive) {
                    val line: String;
                    try {
                        line = reader.readLine() ?: break
                    } catch(e: IOException) {
                        Log.d("Bluetooth_chat", "read failed because the socket closed")
                        break
                    }

                    Log.d("Bluetooth_chat", "received from $address $line")

                    val json = try {
                        Json.parseToJsonElement(line).jsonObject
                    } catch (_: Throwable) {
                        continue
                    }

                    routePacket(json)
                }
            } catch (_: CancellationException) {
                // expected
            } catch (e: Throwable) {
                Log.e("Bluetooth_chat", "reader failed", e)
            } finally {
                disconnect()
            }
        }
    }

    private suspend fun routePacket(json: JsonObject) {
        val requestId = json["id"]?.jsonPrimitive?.content

        if (requestId != null) {
            val continuation = pendingMutex.withLock {
                pending.remove(requestId)
            }

            if (continuation != null) {
                continuation.resume(json)
                return
            }
        }

        onReceive?.invoke(this, json)
        _incoming.emit(json)
    }

    override suspend fun sendAndWait(packet: JsonObject, timeoutMillis: Long): JsonObject? {
        return try {
            val requestId = packet["id"]?.jsonPrimitive?.content
                ?: throw IllegalArgumentException("packet must have an 'id' field")

            send(packet)

            withTimeout(timeoutMillis) {
                waitForResponse(requestId)
            }

        } catch (e: Exception) {
            Log.e("Bluetooth_chat", "sendAndWait failed for ${socket.remoteDevice.address}", e)
            disconnect()
            null
        }
    }

    private fun updateConnectStatus() {
        val connected = socket.isConnected
        if (_isConnected.value != connected) {
            _isConnected.value = connected
        }
    }
}
