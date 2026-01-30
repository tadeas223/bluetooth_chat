package com.example.bluetooth_chat.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.bluetooth_chat.domain.model.bluetooth.Device

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDevice(): Device {
    return Device(
        name = name,
        address = address
    )
}