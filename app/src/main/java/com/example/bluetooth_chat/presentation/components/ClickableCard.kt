package com.example.bluetooth_chat.presentation.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.presentation.theme.MochaBlue
import com.example.bluetooth_chat.presentation.theme.MochaMantle
import com.example.bluetooth_chat.presentation.theme.MochaSky

@Composable
fun ClickableCard(
    cardItems: List<ClickableCardItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(vertical = 8.dp)) {
        items(cardItems) { item ->

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = { item.onClick() }
                    )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPressed) MochaBlue.copy(alpha = 0.3f) else MochaMantle
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // remove shadow
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MochaSky,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
