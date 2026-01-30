package com.example.bluetooth_chat.data.repository

import com.example.bluetooth_chat.domain.model.Contact
import com.example.bluetooth_chat.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomContactRepository @Inject constructor(
    private val dao: ContactDao
): ContactRepository {
    override suspend fun insert(contact: Contact) {
        dao.insert(contact.toEntity())
    }

    override fun selectAll(): Flow<List<Contact>> {
        return dao.selectAll().map { list -> list.map {it.toContact() }}
    }

    override suspend fun insertAll(vararg contact: Contact) {
        dao.insertAll(*contact.map { it.toEntity() }.toTypedArray())
    }

    override fun selectByName(name: String): Flow<Contact> {
        return dao.selectByName(name).map { it.toContact() }
    }

    override fun selectByAddress(address: String): Flow<Contact?> {
        return dao.selectByAddress(address).map {it?.toContact() }
    }

    override suspend fun update(
        contact: Contact
    ) {
        dao.update(contact.toEntity())
    }

    override suspend fun delete(contact: Contact) {
        dao.delete(contact.toEntity())
    }

    override fun selectById(id: Int): Flow<Contact> {
        return dao.selectById(id).map {it.toContact()}
    }
}