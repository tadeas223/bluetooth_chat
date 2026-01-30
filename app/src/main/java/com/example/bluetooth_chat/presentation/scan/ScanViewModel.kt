package com.example.bluetooth_chat.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.paging.util.INITIAL_ITEM_COUNT
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AdvertiseAcceptPacket
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AdvertisePacket
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothScanService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import javax.inject.Inject

data class ScanUiState (
    val devices: List<Device> = emptyList(),
    val showAlert: Boolean = false,
    val navigateBack: Boolean = false,
    val isLoading: Boolean = false
) {}

@HiltViewModel
class ScanViewModel @Inject constructor(
    val bluetoothScanService: BluetoothScanService,
    val bluetoothConnectService: BluetoothConnectService
): ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState>
        get() = _uiState.asStateFlow()

    init {
        bluetoothScanService.startScan()

        bluetoothScanService.devices.onEach { scannedDevices ->
            _uiState.value = _uiState.value.copy(devices = scannedDevices.filter { device ->
                device.name != null
            });
        }.launchIn(viewModelScope)

    }

    fun resetAlert() {
        _uiState.value= _uiState.value.copy(showAlert = false);
    }

    fun onDeviceClicked(device: Device) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            withContext(Dispatchers.IO) {
                val connection = bluetoothConnectService.createConnection(device.address);
                try {
                    connection.connect();
                } catch(e: IOException) {
                    _uiState.value = _uiState.value.copy(showAlert = true)
                    return@withContext
                }

                connection.send(AdvertisePacket().serialize())
                for(i in 1..10) {
                    val response: JsonObject
                    try {
                        response = connection.waitForResponse()
                    } catch(e: IOException) {
                        _uiState.value = _uiState.value.copy(showAlert = true)
                        continue;
                    }

                    if(response["type"]!!.jsonPrimitive.content == "advertise_accept") {
                        val packet = AdvertiseAcceptPacket.deserialize(response);
                        if(packet.accepted) {
                            _uiState.value = _uiState.value.copy(navigateBack = true)
                        } else {
                            _uiState.value = _uiState.value.copy(showAlert = true)
                        }
                    }
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    override fun onCleared() {
        bluetoothScanService.stopScan()
    }
}