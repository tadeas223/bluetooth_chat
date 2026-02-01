package com.example.bluetooth_chat.presentation.profile

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(): ViewModel() {
    fun makeDiscoverable(activity: Activity?, durationSeconds: Int = 300) {
        Log.d("Bluetooth_chat", "AddUserViewModel.makeDiscoverable")
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, durationSeconds)
        }
        activity?.startActivity(discoverableIntent)
    }
}