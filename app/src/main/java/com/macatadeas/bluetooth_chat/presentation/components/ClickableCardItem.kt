package com.macatadeas.bluetooth_chat.presentation.components

import androidx.compose.runtime.Composable

data class ClickableCardItem(
    val onClick: () -> Unit,
    val content: @Composable () -> Unit
)