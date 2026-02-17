package com.example.bluetooth_chat.presentation.add_user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserView(
    navController: NavHostController,
    deviceAddress: String,
    deviceName: String,
    modifier: Modifier = Modifier,
    viewModel: AddUserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }

    LaunchedEffect("AddUserView") {
        viewModel.setDevice(deviceAddress, deviceName)
    }

    if(uiState.showExistsAlert) {
        AlertDialog(
            onDismissRequest = { false },
            title = { Text("error") },
            text = { Text("contact is already saved") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAlerts()
                    navController.popBackStack()
                }) {
                    Text("OK")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }

    if(uiState.done) {
        navController.navigate("contacts")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("add new contact") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            uiState.device?.let { device ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = deviceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = deviceAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contact name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.addUser(username) },
                modifier = Modifier.fillMaxWidth(),
                enabled = username.isNotBlank()
            ) {
                Text("Confirm")
            }
        }
    }
}
