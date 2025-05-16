package com.matin.happychat.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.R

private const val INPUT_ICON_SIZE = 32
private const val MESSAGE_INPUT_TEXT_SIZE = 22

@Composable
fun MessageInputBar(
    message: String,
    isSendButtonEnabled: Boolean,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            MessageTextField(
                value = message,
                onValueChange = onMessageChange,
                onSendClick = onSendClick,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SendButton(onClick = onSendClick, isEnabled = isSendButtonEnabled)
            }
        }
    }
}

@Composable
private fun MessageTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.onPrimary,
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = MESSAGE_INPUT_TEXT_SIZE.sp,
            color = MaterialTheme.colorScheme.onPrimary
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done  // For chat input
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSendClick() }
        ),
        placeholder = {
            Text(
                text = "Type a message...",
                color = MaterialTheme.colorScheme.outline,
                fontSize = MESSAGE_INPUT_TEXT_SIZE.sp
            )
        }
    )
}

@Composable
private fun SendButton(onClick: () -> Unit, isEnabled: Boolean) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape),
        enabled = isEnabled
    ) {
        Icon(
            modifier = Modifier.size(INPUT_ICON_SIZE.dp),
            painter = painterResource(id = R.drawable.ic_send),
            tint = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
            contentDescription = "Send message"
        )
    }
}