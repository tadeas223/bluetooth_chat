package com.example.bluetooth_chat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BluetoothChatApplication : Application() {
    override fun onCreate() {
        createNotificationChannel(this)
    }
}