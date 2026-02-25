package com.example.bluetooth_chat.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.example.bluetooth_chat.BluetoothForegroundService
import com.example.bluetooth_chat.isServiceRunning
import com.example.bluetooth_chat.notificationChannelExists
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val isServerRunning: Boolean = false,
    val notificationsEnabled: Boolean = false
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext val context: Context,
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState>
        get() = _uiState.asStateFlow()

    init {
        _uiState.value= _uiState.value.copy(
            isServerRunning = context.isServiceRunning(BluetoothForegroundService::class.java),
            notificationsEnabled = notificationChannelExists(context)
        )
    }

    fun makeDiscoverable(activity: Activity?, durationSeconds: Int = 300) {
        Log.d("Bluetooth_chat", "AddUserViewModel.makeDiscoverable")
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, durationSeconds)
        }
        activity?.startActivity(discoverableIntent)
    }

    fun setNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            notificationsEnabled = notificationChannelExists(context)
        )
    }
}