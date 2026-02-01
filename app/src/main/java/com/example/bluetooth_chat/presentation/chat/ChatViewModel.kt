package com.example.bluetooth_chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String

data class ChatUiState(
    val contact: Contact? = null,
    val messages: List<ChatMessage> = emptyList()
) {}
@HiltViewModel
class ChatViewModel @Inject constructor(
    val contactRepository: ContactRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(ChatUiState());
    val uiState: StateFlow<ChatUiState>
        get() = _uiState.asStateFlow()

    fun setContact(id: Int) {
        viewModelScope.launch {
            val contact = contactRepository.selectById(id)
            _uiState.value = _uiState.value.copy(contact = contact.first())
        }
    }

    fun sendMessage(msg: String) {
        TODO("Not Implemented")
    }
}