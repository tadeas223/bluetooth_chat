package com.example.bluetooth_chat.data.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.content.Context
import android.util.Log
import com.example.bluetooth_chat.data.bluetooth.BluetoothConnection
import com.example.bluetooth_chat.data.bluetooth.toDevice
import com.example.bluetooth_chat.data.hasPermissions
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class AndroidBluetoothConnectService @Inject constructor(
    @ApplicationContext val context: Context,
): BluetoothConnectService {
    companion object {
        const val SERVICE_UUID = "2bf1c8cf-f803-440d-8c91-b51f5115f232"
    }

    override val requiredPermissions = listOf(
        Manifest.permission.BLUETOOTH_CONNECT,
    )

    private val _activeConnections = MutableStateFlow<Map<Device, Connection>>(emptyMap())
    override val activeConnections
        get() = _activeConnections.asStateFlow()

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var serverJob: Job? = null

    private var onReceive: ((Connection, JsonObject) -> Unit)? = null

    override fun onReceive(callback: ((Connection, JsonObject) -> Unit)?) {
        onReceive = callback
    }

    override fun startServer() {
        if(!hasPermissions(context, requiredPermissions))  {
            throw SecurityException("missing required permissions")
        }

        serverJob = CoroutineScope(Dispatchers.IO).launch {
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            Log.d("Bluetooth_chat", "server started");
            while (isActive) {
                val clientSocket = try {
                    currentServerSocket?.accept()
                } catch (_: IOException) {
                    break
                }

                if(clientSocket != null) {
                    val device = clientSocket.remoteDevice.toDevice()
                    val connection = BluetoothConnection(clientSocket)
                    try {
                        connection.connect()
                    } catch(e: IOException) {
                        Log.d("Bluetooth_chat", "failed to connect device to the server: ${device.address}");
                        continue
                    }

                    Log.d("Bluetooth_chat", "device connected to the server: ${device.address}");

                    connection.onDisconnect {
                        val current = _activeConnections.value.toMutableMap()
                        Log.d("Bluetooth_chat", "device disconnected from the server ${device.address}");
                        var device: Device? = null
                        current.forEach { entry ->
                            if(entry.value == connection) {
                                device = entry.key
                                return@forEach
                            }
                        }

                        if(device != null) {
                            current.remove(device)
                        }

                        _activeConnections.value = current.toMap()
                    }

                    connection.onReceive(onReceive)

                    val current = _activeConnections.value.toMutableMap()
                    current[device] = connection
                    _activeConnections.value = current.toMap()
                }
            }
        }
    }

    override fun stopServer() {
        if(!hasPermissions(context, requiredPermissions))  {
            throw SecurityException("missing required permissions")
        }

        serverJob?.cancel()
        currentServerSocket?.close()
    }

    override suspend fun createConnection(address: String): Connection {
        if(!hasPermissions(context, requiredPermissions))  {
            throw SecurityException("missing required permissions")
        }

        if(bluetoothAdapter == null) {
            throw SecurityException("could not get BluetoothAdapter, probably missing permissions")
        }

        val blDevice = bluetoothAdapter!!.getRemoteDevice(address)
        val socket = blDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
        return BluetoothConnection(socket)
    }
}