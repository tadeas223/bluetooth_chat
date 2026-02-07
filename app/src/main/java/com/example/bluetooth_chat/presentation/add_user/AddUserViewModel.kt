package com.example.bluetooth_chat.presentation.add_user

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddUserUiState(
    val device: Device? = null,
    val done: Boolean = false
) {}

@HiltViewModel
class AddUserViewModel @Inject constructor(
    val contactRepository: ContactRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddUserUiState>(AddUserUiState())
    val uiState: StateFlow<AddUserUiState>
        get() = _uiState.asStateFlow()

    fun setDevice(address: String, name: String) {
        _uiState.value = _uiState.value.copy(device = Device(address, name));
    }

    fun addUser(username: String) {
        viewModelScope.launch {
            if(_uiState.value.device == null) {
                return@launch
            }

            contactRepository.insert(Contact(0, _uiState.value.device!!.address, username))

            _uiState.value = _uiState.value.copy(done = true)
        }
    }
}