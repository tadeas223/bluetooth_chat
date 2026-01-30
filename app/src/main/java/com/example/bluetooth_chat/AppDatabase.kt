package com.example.bluetoothchat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.bluetooth_chat.data.repository.ContactEntity
import com.example.bluetooth_chat.data.repository.ContactDao

@Database(entities = [ContactEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

