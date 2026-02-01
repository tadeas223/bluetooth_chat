package com.example.bluetooth_chat.presentation.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bluetooth_chat.presentation.components.ClickableCard
import com.example.bluetooth_chat.presentation.components.ClickableCardItem
import com.example.bluetooth_chat.presentation.theme.MochaSubtext1

@Composable
fun ScanView(navController: NavController,
             viewModel: ScanViewModel = hiltViewModel<ScanViewModel>(),
             modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    if(uiState.navigateBack) {
        navController.navigate("contacts") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId)
        }
    }

    if(uiState.showAlert) {
        AlertDialog(
            onDismissRequest = { false },
            title = { Text("error") },
            text = { Text("device rejected your request") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetAlert() }) {
                    Text("OK")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }

    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "asking the device",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MochaSubtext1
                )

                Spacer(modifier = Modifier.height(40.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        uiState.devices.isEmpty() -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "scanning for devices",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MochaSubtext1
                )

                Spacer(modifier = Modifier.height(40.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        else -> {
            val cardItems = uiState.devices.map { device ->
                ClickableCardItem(
                    text = "${device.name}",
                    onClick = { viewModel.onDeviceClicked(device) }
                )
            }

            ClickableCard(cardItems = cardItems, modifier = modifier.fillMaxSize())
        }
    }
}