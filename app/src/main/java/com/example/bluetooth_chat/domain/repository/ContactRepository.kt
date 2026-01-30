package com.example.bluetooth_chat.domain.repository

import com.example.bluetooth_chat.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun insert(contact: Contact)
    suspend fun insertAll(vararg contact: Contact)

    fun selectById(id: Int): Flow<Contact>
    fun selectAll(): Flow<List<Contact>>
    fun selectByName(name: String): Flow<Contact>
    fun selectByAddress(address: String): Flow<Contact?>
    suspend fun update(contact: Contact)
    suspend fun delete(contact: Contact)
}