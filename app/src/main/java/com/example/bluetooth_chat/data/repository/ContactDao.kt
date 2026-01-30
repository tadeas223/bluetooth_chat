package com.example.bluetooth_chat.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact WHERE :name = username")
    fun selectByName(name: String): Flow<ContactEntity>

    @Query("SELECT * FROM contact WHERE :id = id")
    fun selectById(id: Int): Flow<ContactEntity>

    @Query("SELECT * FROM contact WHERE :address = address")
    fun selectByAddress(address: String): Flow<ContactEntity?>

    @Query("SELECT * FROM contact")
    fun selectAll(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg contactEntity: ContactEntity)

    @Update
    suspend fun update(vararg contactEntity: ContactEntity)

    @Delete
    suspend fun delete(contactEntity: ContactEntity)
}