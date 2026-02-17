package com.example.bluetooth_chat.presentation.contacts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState (
    var contacts: List<Contact>
) {}

@HiltViewModel
class ContactsViewModel @Inject constructor(
    val contactRepository: ContactRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ContactsUiState(emptyList()))
    val uiState: StateFlow<ContactsUiState>
      get() = _uiState.asStateFlow()

    init {
        contactRepository.selectAll().onEach {
            _uiState.value = _uiState.value.copy(contacts = it);
        }.launchIn(viewModelScope)
    }
}