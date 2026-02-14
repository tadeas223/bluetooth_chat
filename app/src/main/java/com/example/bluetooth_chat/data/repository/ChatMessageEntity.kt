package com.example.bluetooth_chat.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "msg")
data class ChatMessageEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val text: String,

    val contactId: Int,

    val isLocal: Boolean
){}