package com.example.bluetooth_chat.domain.model

data class ChatMessage(
    val id: Int,
    val isLocal: Boolean,
    val contact: Contact,
    val text: String,
) {}