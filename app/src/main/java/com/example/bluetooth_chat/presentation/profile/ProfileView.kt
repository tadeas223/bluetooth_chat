package com.example.bluetooth_chat.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bluetooth_chat.closeNotificationChannel
import com.example.bluetooth_chat.createNotificationChannel
import com.example.bluetooth_chat.presentation.components.ClickableCard
import com.example.bluetooth_chat.presentation.components.ClickableCardItem
import com.example.bluetooth_chat.presentation.contacts.ContactsViewModel

@Composable
fun ProfileView(navController: NavHostController,
                viewModel: ProfileViewModel= hiltViewModel<ProfileViewModel>(),
                modifier: Modifier = Modifier) {

    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotifications(isGranted)
    }

    ClickableCard(cardItems = listOf(
            ClickableCardItem(onClick = { navController.navigate("scan") }) {
                Text("scan for contacts")
            },
            ClickableCardItem(onClick = { viewModel.makeDiscoverable(activity) } ) {
                Text("make discoverable")
            },
            ClickableCardItem(onClick = {}) {
                Text("server is ${if(uiState.isServerRunning) "online" else "offline"}")
            },
            ClickableCardItem(onClick = {}) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "enable notifications")
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = {
                            viewModel.setNotifications(it)
                            if(it) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                createNotificationChannel(context)
                            } else {
                                closeNotificationChannel(context)
                            }
                        },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        ),
        modifier = Modifier.fillMaxSize()
    )
}