package com.macatadeas.bluetooth_chat.data.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey(true) val id: Int,
    @ColumnInfo("username") val username: String,
    @ColumnInfo("address") val address: String
){}