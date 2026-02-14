package com.example.bluetooth_chat.presentation.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AcceptPacket
import com.example.bluetooth_chat.domain.model.bluetooth.packets.AdvertisePacket
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothScanService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class ScanUiState (
    val devices: List<Device> = emptyList(),
    val showAlert: Boolean = false,
    val acceptedDevice: Device? = null,
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

            val success = withContext(Dispatchers.IO) {
                val connection = bluetoothConnectService.createConnection(device.address)

                try {
                    connection.connect()
                } catch (e: IOException) {
                    Log.d("Bluetooth_chat", "failed to connect", e)
                    return@withContext false
                }

                val id = UUID.randomUUID().toString()
                val response = connection.sendAndWait(AdvertisePacket(id).serialize())

                if (response != null && response["type"]?.jsonPrimitive?.content == "accept") {
                    val packet = AcceptPacket.deserialize(response)
                    connection.disconnect()
                    packet.accepted
                } else {
                    connection.disconnect()
                    false
                }
            }

            if (success) {
                Log.d("Bluetooth_chat", "device accepted connection")
                _uiState.value = _uiState.value.copy(acceptedDevice = device)
            } else {
                Log.d("Bluetooth_chat", "device rejected or failed")
                _uiState.value = _uiState.value.copy(showAlert = true, acceptedDevice = null)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    override fun onCleared() {
        bluetoothScanService.stopScan()
    }
}