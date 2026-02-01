package com.example.bluetooth_chat.presentation.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.presentation.theme.MochaBlue
import com.example.bluetooth_chat.presentation.theme.MochaMantle
import com.example.bluetooth_chat.presentation.theme.MochaSubtext1
import com.example.bluetooth_chat.presentation.theme.MochaText

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "Type a message…",
                        color = MochaSubtext1
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MochaMantle,
                    unfocusedContainerColor = MochaMantle,
                    focusedTextColor = MochaText,
                    unfocusedTextColor = MochaText,
                    cursorColor = MochaBlue
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MochaBlue
                )
            }
        }
    }
}