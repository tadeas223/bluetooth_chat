package com.example.bluetooth_chat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.START_STICKY
import androidx.core.app.ServiceCompat.startForeground
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bluetooth_chat.data.repository.RoomChatMessageRepository
import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AcceptPacket
import com.example.bluetooth_chat.domain.repository.ChatMessageRepository
import com.example.bluetooth_chat.domain.repository.ContactRepository
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import java.security.Provider
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class BluetoothForegroundService() : Service() {
    var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    @Inject lateinit var bluetoothConnectService: BluetoothConnectService
    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var chatMessageRepository: ChatMessageRepository
    @Inject @ApplicationContext lateinit var context: Context

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(123321123, createNotification())
        startServer()

        scope.launch {
            handle()
        }

        return START_STICKY
    }
    private fun createNotification(): Notification {
        val channelId = "bluetooth_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bluetooth Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bluetooth Service Running")
            .setContentText("Handling Bluetooth connections")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your icon
            .build()
    }

    suspend fun handle() {
        bluetoothConnectService.incomingPackets.collect { pair ->
            val connection = pair.first;
            val json = pair.second;

            if (json["type"]!!.jsonPrimitive.content == "message") {
                val id = json["id"]!!.jsonPrimitive.content;
                var contact: Contact? = null;

                try {
                    contact = contactRepository.selectByAddress(connection.address).first();
                } catch (e: Exception) {
                    connection.send(AcceptPacket(id, false).serialize());
                    return@collect
                }

                if(contact == null) {
                    connection.send(AcceptPacket(id, false).serialize());
                    return@collect
                }

                try {
                    chatMessageRepository.insert(
                        ChatMessage(
                            id = 0,
                            contact = contact,
                            isLocal = false,
                            text = json["text"]!!.jsonPrimitive.content
                        )
                    )
                } catch (e: Exception) {
                    connection.send(AcceptPacket(id, false).serialize());
                }

                if(!isAppInForeground(context)) {
                    showMessageNotification(context, json["text"]!!.jsonPrimitive.content, contact.username)
                }
                connection.send(AcceptPacket(id, true).serialize());
                return@collect
            }
        }
    }

    fun startServer() {
        bluetoothConnectService.startServer()
        scope.launch(Dispatchers.IO) {
            bluetoothConnectService.bluetoothEnabled.collect { enabled ->
                if (!enabled) {
                    bluetoothConnectService.stopServer()
                    bluetoothConnectService.requestBluetooth()
                    bluetoothConnectService.startServer()
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
    }
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

