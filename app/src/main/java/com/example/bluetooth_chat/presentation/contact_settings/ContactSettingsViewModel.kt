package com.example.bluetooth_chat.presentation.contact_settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.model.bluetooth.Device
import com.example.bluetooth_chat.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactSettingsUiState (
    val contact: Contact? = null,
    val contactDeleted: Boolean = false,
) {}

@HiltViewModel
class ContactSettingsViewModel @Inject constructor(
    val contactRepository: ContactRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactSettingsUiState())
    val uiState: StateFlow<ContactSettingsUiState>
        get() = _uiState.asStateFlow()

    fun setContact(id: Int) {
        viewModelScope.launch {
            val contact = contactRepository.selectById(id).first()
            _uiState.value = _uiState.value.copy(contact = contact)
        }
    }

    fun deleteContact() {
        viewModelScope.launch {
            if(_uiState.value.contact == null) return@launch
            contactRepository.delete(uiState.value.contact!!)
            _uiState.value = _uiState.value.copy(contactDeleted = true)
        }
    }
}