package com.macatadeas.bluetooth_chat.presentation.profile

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macatadeas.bluetooth_chat.BluetoothForegroundService
import com.macatadeas.bluetooth_chat.data.UserPreferences
import com.macatadeas.bluetooth_chat.isServiceRunning
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isServerRunning: Boolean = false,
    val notificationsEnabled: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val userPreferences = UserPreferences(context)

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            isServerRunning = context.isServiceRunning(BluetoothForegroundService::class.java)
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeNotificationPreference()
    }

    private fun observeNotificationPreference() {
        viewModelScope.launch {
            userPreferences.notificationsEnabled.collect { enabled ->
                _uiState.update {
                    it.copy(notificationsEnabled = enabled)
                }
            }
        }
    }

    fun makeDiscoverable(activity: Activity?, durationSeconds: Int = 300) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, durationSeconds)
        }
        activity?.startActivity(discoverableIntent)
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }
}