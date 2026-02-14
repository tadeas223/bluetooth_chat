package com.example.bluetooth_chat.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AcceptPacket
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.Connection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import javax.inject.Inject

data class NavigationUiState (
    val advertisingDevice: Device? = null,
    val advertiseAccepted: Boolean = false
)
{}

@HiltViewModel
class NavigationViewModel @Inject constructor(
    val bluetoothConnectService: BluetoothConnectService
): ViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState>
        get() = _uiState.asStateFlow()

    private var advertiseDecision: CompletableDeferred<Boolean>? = null

    private var advertisingConnection: Connection? = null

    private suspend fun waitForAdvertiseDecision(): Boolean {
        val decision = CompletableDeferred<Boolean>()
        advertiseDecision = decision
        return decision.await()
    }

    init {
        bluetoothConnectService.onReceive { connection, json ->
            if(advertisingConnection != null) {
                return@onReceive
            }

            if(json["type"]!!.jsonPrimitive.content == "advertise") {
                advertisingConnection = connection
                _uiState.value = _uiState.value.copy(
                    advertisingDevice = Device(
                        address = connection.address,
                        name = connection.name
                    ),
                )

                viewModelScope.launch {
                    val id = json["id"]!!.jsonPrimitive.content;
                    val accepted = waitForAdvertiseDecision()

                    if (accepted) {
                        connection.send(AcceptPacket(id,true).serialize())
                        _uiState.value = _uiState.value.copy(advertiseAccepted = true)
                    } else {
                        connection.send(AcceptPacket(id,false).serialize())
                        _uiState.value = _uiState.value.copy(advertiseAccepted = false)
                    }

                    advertisingConnection = null
                }
            }
        }

        bluetoothConnectService.startServer()
    }

    fun alertDismiss() {
        advertiseDecision?.complete(false)
        advertiseDecision = null

        resetAlert()
    }

    fun alertConfirm() {
        advertiseDecision?.complete(true)
        advertiseDecision = null

        resetAlert()
    }

    fun resetAlert() {
        _uiState.value = _uiState.value.copy(
            advertisingDevice = null,
            advertiseAccepted = false
        )
    }

    override fun onCleared() {
        super.onCleared()

        bluetoothConnectService.onReceive(null)
        bluetoothConnectService.stopServer()
    }
}