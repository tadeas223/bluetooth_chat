package com.macatadeas.bluetooth_chat.presentation.profile

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.macatadeas.bluetooth_chat.presentation.components.ClickableCard
import com.macatadeas.bluetooth_chat.presentation.components.ClickableCardItem

@Composable
fun ProfileView(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val notificationsAllowedBySystem =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotifications(isGranted)
    }

    ClickableCard(
        cardItems = listOf(

            ClickableCardItem(onClick = { navController.navigate("scan") }) {
                Text("scan for contacts")
            },

            ClickableCardItem(onClick = { viewModel.makeDiscoverable(activity) }) {
                Text("make discoverable")
            },

            ClickableCardItem(onClick = {}) {
                Text("server is ${if (uiState.isServerRunning) "online" else "offline"}")
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
                        onCheckedChange = { enabled ->

                            if (enabled) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                                    val permissionGranted =
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED

                                    if (permissionGranted) {
                                        viewModel.setNotifications(true)
                                    } else {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }

                                } else {
                                    viewModel.setNotifications(true)
                                }

                            } else {
                                viewModel.setNotifications(false)
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