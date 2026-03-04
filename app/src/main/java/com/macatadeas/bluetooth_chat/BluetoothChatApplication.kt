package com.macatadeas.bluetooth_chat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BluetoothChatApplication : Application() {
    override fun onCreate() {
        createNotificationChannel(this)
    }
}