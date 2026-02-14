package com.example.bluetooth_chat.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AcceptPacket
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AdvertisePacket
import com.example.bluetooth_chat.domain.model.bluetooth.packets.MessagePacket
import com.example.bluetooth_chat.domain.repository.ContactRepository
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.String

data class ChatUiState(
    val contact: Contact? = null,
    val connected: Boolean = false,
    val sendFailed: String? = null,
    val connectionFailed: Boolean = false,
    val connecting: Boolean = false,
    val messages: List<ChatMessage> = emptyList()
) {}
@HiltViewModel
class ChatViewModel @Inject constructor(
    val contactRepository: ContactRepository,
    val bluetoothConnectService: BluetoothConnectService
): ViewModel(){
    private val _uiState = MutableStateFlow(ChatUiState());
    val uiState: StateFlow<ChatUiState>
        get() = _uiState.asStateFlow()

    var connection: Connection? = null

    fun setContact(id: Int) {
        viewModelScope.launch {
            val contact = contactRepository.selectById(id)
            _uiState.value = _uiState.value.copy(contact = contact.first())

            connection = bluetoothConnectService.createConnection(_uiState.value.contact!!.address)
            tryReconnect()
        }
    }

    fun tryReconnect() {
        val conn = connection ?: run {
            _uiState.value = _uiState.value.copy(connectionFailed = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(connecting = true, connectionFailed = false)

            try {
                conn.connect()

                conn.isConnected
                    .onEach { _uiState.value = _uiState.value.copy(connected = it) }
                    .launchIn(this)

            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(connectionFailed = true, connected = false)
            } finally {
                _uiState.value = _uiState.value.copy(connecting = false)
            }
        }
    }

    fun sendMessage(msg: String) {
        if(connection == null) {
            _uiState.value = _uiState.value.copy(sendFailed = "contact is offline")
            return
        }

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                val id = UUID.randomUUID().toString()
                var response: JsonObject? = null;
                try {
                    response = connection!!.sendAndWait(MessagePacket(id, msg).serialize())
                } catch (e: IOException) { }

                if (response != null && response["type"]?.jsonPrimitive?.content == "accept") {
                    val packet = AcceptPacket.deserialize(response)
                    packet.accepted
                } else {
                    false
                }
            }

            if (success) {
                Log.d("Bluetooth_chat", "message sent")
            } else {
                Log.d("Bluetooth_chat", "failed to send")
                _uiState.value = _uiState.value.copy(sendFailed = "failed to send")
            }
        }
    }

    fun resetConnectionFailed() {
        _uiState.value = _uiState.value.copy(connectionFailed = false)
    }

    fun resetSendFailedMsg() {
        _uiState.value = _uiState.value.copy(sendFailed = null)
    }
}