package com.macatadeas.bluetooth_chat.data.repository

import com.macatadeas.bluetooth_chat.domain.model.ChatMessage
import com.macatadeas.bluetooth_chat.domain.repository.ContactRepository
import kotlinx.coroutines.flow.first

suspend fun ChatMessageEntity.toChatMessage(contactRepository: ContactRepository): ChatMessage {
    return ChatMessage(
        id = this.id,
        contact = contactRepository.selectById(this.contactId).first(),
        text = this.text,
        isLocal = this.isLocal,
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = this.id,
        contactId = this.contact.id,
        text = this.text,
        isLocal = this.isLocal
    )
}
