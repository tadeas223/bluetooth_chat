package com.example.bluetooth_chat.data

import android.content.Context
import android.content.pm.PackageManager

fun hasPermission(context: Context, permission: String): Boolean {
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun hasPermissions(context: Context, permissions: List<String>): Boolean {
    permissions.forEach { perm ->
        if(context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }

    return true
}
