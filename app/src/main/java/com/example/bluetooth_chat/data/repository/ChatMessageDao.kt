package com.example.bluetooth_chat.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bluetooth_chat.data.repository.ChatMessageEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg message: ChatMessageEntity)


    @Update
    fun update(message: ChatMessageEntity)

    @Delete
    fun delete(message: ChatMessageEntity)

    @Query("SELECT * FROM msg")
    fun selectAll(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM msg WHERE id = :id")
    fun selectById(id: Int): Flow<ChatMessageEntity>

    @Query("SELECT * FROM msg WHERE contactId = :id")
    fun selectByContactId(id: Int): Flow<List<ChatMessageEntity>>
}