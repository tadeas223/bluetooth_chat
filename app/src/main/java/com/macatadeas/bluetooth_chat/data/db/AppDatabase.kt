package com.macatadeas.bluetooth_chat.data.db

import com.macatadeas.bluetooth_chat.data.repository.ContactEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import com.macatadeas.bluetooth_chat.data.repository.ChatMessageDao
import com.macatadeas.bluetooth_chat.data.repository.ChatMessageEntity
import com.macatadeas.bluetooth_chat.data.repository.ContactDao

@Database(entities = [ContactEntity::class, ChatMessageEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun chatMessageDao(): ChatMessageDao
}

