package com.macatadeas.bluetooth_chat.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

    val notificationsEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[NOTIFICATIONS_ENABLED] ?: false
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}