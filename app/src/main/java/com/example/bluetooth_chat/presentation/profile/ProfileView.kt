package com.example.bluetooth_chat.presentation.profile

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bluetooth_chat.presentation.components.ClickableCard
import com.example.bluetooth_chat.presentation.components.ClickableCardItem
import com.example.bluetooth_chat.presentation.contacts.ContactsViewModel

@Composable
fun ProfileView(navController: NavHostController,
                viewModel: ProfileViewModel= hiltViewModel<ProfileViewModel>(),
                modifier: Modifier = Modifier) {

    val activity = LocalContext.current as? Activity

    ClickableCard(cardItems = listOf(
        ClickableCardItem("scan for contacts", {
            navController.navigate("scan")
        }),
        ClickableCardItem("make discoverable", {
            viewModel.makeDiscoverable(activity)
        })

    ), modifier = modifier.fillMaxSize())
}