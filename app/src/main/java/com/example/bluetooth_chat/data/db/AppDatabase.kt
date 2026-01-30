package com.example.bluetooth_chat.data.db

import com.example.bluetooth_chat.data.repository.ContactEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bluetooth_chat.data.repository.ContactDao

@Database(entities = [ContactEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

