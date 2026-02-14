package com.example.bluetooth_chat.domain.repository

import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ChatMessageRepository {
    suspend fun insert(message: ChatMessage)
    suspend fun insertAll(vararg message: ChatMessage)

    fun selectById(id: Int): Flow<ChatMessage>
    fun selectAll(): Flow<List<ChatMessage>>
    fun selectByContact(contact: Contact): Flow<List<ChatMessage>>
    fun selectByContactId(id: Int): Flow<List<ChatMessage>>


    suspend fun update(message: ChatMessage)
    suspend fun delete(message: ChatMessage)
}