package com.example.bluetooth_chat.data.repository

import com.example.bluetooth_chat.domain.model.ChatMessage
import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.repository.ChatMessageRepository
import com.example.bluetooth_chat.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomChatMessageRepository @Inject constructor(
    val dao: ChatMessageDao,
    val contactRepository: ContactRepository
): ChatMessageRepository {
    override suspend fun insert(message: ChatMessage) {
        dao.insert(message.toEntity())
    }

    override suspend fun insertAll(vararg message: ChatMessage) {
        dao.insertAll(*message.map { it.toEntity() }.toTypedArray())
    }

    override fun selectById(id: Int): Flow<ChatMessage> {
        return dao.selectById(id).map {it.toChatMessage(contactRepository)}
    }

    override fun selectAll(): Flow<List<ChatMessage>> {
        return dao.selectAll().map { list -> list.map { it.toChatMessage(contactRepository) } }
    }

    override fun selectByContact(contact: Contact): Flow<List<ChatMessage>> {
        return dao.selectByContactId(contact.id).map { list -> list.map { it.toChatMessage(contactRepository) }}
    }

    override suspend fun update(message: ChatMessage) {
        return dao.update(message.toEntity())
    }

    override suspend fun delete(message: ChatMessage) {
        return dao.delete(message.toEntity())
    }

    override fun selectByContactId(id: Int): Flow<List<ChatMessage>> {
        return dao.selectByContactId(id).map {list -> list.map { it.toChatMessage(contactRepository) }}
    }
}