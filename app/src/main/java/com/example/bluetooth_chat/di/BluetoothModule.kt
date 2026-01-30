package com.example.bluetooth_chat.di

import android.content.Context
import com.example.bluetooth_chat.data.bluetooth.service.AndroidBluetoothConnectService
import com.example.bluetooth_chat.data.bluetooth.service.AndroidBluetoothScanService
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothConnectService
import com.example.bluetooth_chat.domain.service.bluetooth.BluetoothScanService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {
    @Provides
    @Singleton
    fun provideBluetoothScanService(@ApplicationContext context: Context): BluetoothScanService {
        return AndroidBluetoothScanService(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothConnectService(@ApplicationContext context: Context): BluetoothConnectService {
        return AndroidBluetoothConnectService(context)
    }

}