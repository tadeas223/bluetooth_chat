package com.macatadeas.bluetooth_chat.presentation.contact_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.macatadeas.bluetooth_chat.presentation.components.ClickableCard
import com.macatadeas.bluetooth_chat.presentation.components.ClickableCardItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSettingsView (
    contactId: Int,
    navController: NavHostController,
    viewModel: ContactSettingsViewModel = hiltViewModel<ContactSettingsViewModel>(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect("ContactSettingsView") {
        viewModel.setContact(contactId)
    }

    if(uiState.contactDeleted) {
        navController.navigate("contacts")
    }

    if(uiState.contact == null) {
        Text("loading")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.contact!!.username,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable { navController.popBackStack() },
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.height(100.dp)
            )

            ClickableCard(
                cardItems = listOf(
                    ClickableCardItem(onClick = {}) {
                        Text(uiState.contact!!.address)
                    },
                    ClickableCardItem(onClick = { viewModel.deleteContact() }) {
                        Text("delete contact")
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}