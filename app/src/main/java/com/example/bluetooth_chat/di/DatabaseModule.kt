package com.example.bluetooth_chat.di

import android.app.Application
import androidx.room.Room
import com.example.bluetooth_chat.data.repository.ChatMessageDao
import com.example.bluetooth_chat.data.repository.ContactDao
import com.example.bluetooth_chat.data.repository.RoomChatMessageRepository
import com.example.bluetooth_chat.data.repository.RoomContactRepository
import com.example.bluetooth_chat.domain.repository.ChatMessageRepository
import com.example.bluetooth_chat.domain.repository.ContactRepository
import com.example.bluetooth_chat.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Singleton
    @Provides
    fun provideChatMessageRepository(dao: ChatMessageDao, contactRepository: ContactRepository): ChatMessageRepository =
        RoomChatMessageRepository(dao, contactRepository)

    @Singleton
    @Provides
    fun provideContactRepository(dao: ContactDao): ContactRepository =
        RoomContactRepository(dao)
}