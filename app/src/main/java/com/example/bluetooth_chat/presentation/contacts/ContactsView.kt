package com.example.bluetooth_chat.presentation.contacts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bluetooth_chat.presentation.components.ClickableCard
import com.example.bluetooth_chat.presentation.components.ClickableCardItem
import com.example.bluetooth_chat.presentation.theme.MochaSubtext1

@Composable
fun ContactsView(
    navController: NavController,
    viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.contacts.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "no contacts found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MochaSubtext1
                )
            }
        }
        else -> {
            val cardItems = uiState.contacts.map { contact ->
                ClickableCardItem(
                    text = "${contact.username} • offline",
                    onClick = { viewModel.onContactClicked(contact) }
                )
            }

            ClickableCard(cardItems = cardItems, modifier = modifier.fillMaxSize())
        }
    }
}
