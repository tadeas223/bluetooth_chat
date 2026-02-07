package com.example.bluetooth_chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AcceptPacket
import com.example.bluetooth_chat.domain.model.bluetooth.packets.MessagePacket
import com.example.bluetooth_chat.domain.repository.ContactRepository
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.String

data class ChatUiState(
    val contact: Contact? = null,
    val connected: Boolean = false,
    val sendFailed: String? = null,
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
            try {
                connection!!.connect()

                connection!!.isConnected.collect {
                    _uiState.value = _uiState.value.copy(connected = it)
                }
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(connected = false)
            }
        }
    }

    fun sendMessage(msg: String) {
        if(connection == null) return

        viewModelScope.launch {
            if(connection!!.isConnected.value) {
                val id = UUID.randomUUID().toString()
                connection!!.send(MessagePacket(id, msg).serialize())

                val response = try {
                    connection!!.waitForResponse(id)
                } catch(e: CancellationException) {
                    connection!!.disconnect()
                    _uiState.value = _uiState.value.copy(sendFailed = "connection lost")
                    return@launch
                }

                if(response["id"]?.jsonPrimitive?.content == "accept") {
                    val packet = AcceptPacket.deserialize(response)
                    if(!packet.accepted) {
                        _uiState.value = _uiState.value.copy(sendFailed = "contact rejected your message")
                    }
                } else {

                }
                connection!!.disconnect()
            } else {
                _uiState.value = _uiState.value.copy(sendFailed = "contact is not connected")
            }
        }
    }

    fun resetSendFailedMsg() {
        _uiState.value = _uiState.value.copy(sendFailed = null)
    }
}