package com.example.bluetooth_chat.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class ClickableCardItem(
    val onClick: () -> Unit,
    val content: @Composable () -> Unit
)