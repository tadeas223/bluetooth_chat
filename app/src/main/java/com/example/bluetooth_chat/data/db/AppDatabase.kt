package com.example.bluetooth_chat.data.db

import com.example.bluetooth_chat.data.repository.ContactEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bluetooth_chat.data.repository.ChatMessageDao
import com.example.bluetooth_chat.data.repository.ChatMessageEntity
import com.example.bluetooth_chat.data.repository.ContactDao

@Database(entities = [ContactEntity::class, ChatMessageEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun chatMessageDao(): ChatMessageDao
}

