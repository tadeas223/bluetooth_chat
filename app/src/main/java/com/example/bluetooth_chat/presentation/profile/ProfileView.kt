package com.example.bluetooth_chat.presentation.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bluetooth_chat.presentation.components.ClickableCard
import com.example.bluetooth_chat.presentation.components.ClickableCardItem
import com.example.bluetooth_chat.presentation.contacts.ContactsViewModel

@Composable
fun ProfileView(navController: NavHostController,
                viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>(),
                modifier: Modifier = Modifier) {

    ClickableCard(cardItems = listOf(
        ClickableCardItem("scan for contacts", {
            navController.navigate("scan")
        })

    ), modifier = modifier.fillMaxSize())
}