package com.example.bluetooth_chat.data.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bluetooth_chat.TaskProcessor
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    @RequiresApi(Build.VERSION_CODES.S)
    override val requiredPermissions = listOf(
        Manifest.permission.BLUETOOTH_CONNECT,
    )

    private var onBluetoothOff: (() -> Unit)? = null;

    private val _activeConnections = MutableStateFlow<Map<Device, Connection>>(emptyMap())
    override val activeConnections
        get() = _activeConnections.asStateFlow()

    private val _incomingPackets = MutableSharedFlow<Pair<Connection, JsonObject>>(
        replay = 0,
        extraBufferCapacity = 16
    )
    override val incomingPackets: SharedFlow<Pair<Connection, JsonObject>>
        get() = _incomingPackets.asSharedFlow()

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var serverJob: Job? = null
    private var emitProcessor: TaskProcessor = TaskProcessor()

    override val bluetoothEnabled: Flow<Boolean> = callbackFlow {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        trySend(adapter.isEnabled)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    trySend(state == BluetoothAdapter.STATE_ON)
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitClose { context.unregisterReceiver(receiver) }
    }

    override fun requestBluetooth() {
        val adapter = bluetoothAdapter ?: return
        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
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
                    onBluetoothOff?.invoke()
                    stopServer();
                    break;
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

                        _activeConnections.value= current.toMap()
                    }

                    connection.onReceive { con, json ->
                        _incomingPackets.tryEmit(Pair(con, json))
                    }

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
        emitProcessor.stop()
        currentServerSocket?.close()
    }

    override suspend fun createConnection(address: String): Connection {
        if(!hasPermissions(context, requiredPermissions))  {
            throw SecurityException("missing required permissions")
        }

        if(bluetoothAdapter == null) {
            throw SecurityException("could not get BluetoothAdapter, probably missing permissions")
        }

        val socket = try {
            val blDevice = bluetoothAdapter!!.getRemoteDevice(address)
            blDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
        } catch(e: IOException) {
            onBluetoothOff?.invoke();
            stopServer();
            throw e;
        }
        return BluetoothConnection(socket)
    }
}