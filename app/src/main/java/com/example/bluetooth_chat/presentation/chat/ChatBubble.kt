package com.example.bluetooth_chat.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.presentation.theme.MochaBlue
import com.example.bluetooth_chat.presentation.theme.MochaMantle
import com.example.bluetooth_chat.presentation.theme.MochaText

@Composable
fun ChatBubble(
    text: String,
    isMine: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMine) MochaBlue else MochaMantle,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = MochaText
            )
        }
    }
}
