package com.matin.happychat.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.R

private const val INPUT_ICON_SIZE = 32
private const val RECORDING_ANIMATION_DURATION = 500
private const val MESSAGE_INPUT_TEXT_SIZE = 22

/**
 * Message input bar at the bottom of the chat screen
 */
@Composable
fun MessageInputBar(
    message: String,
    isRecording: Boolean,
    showSendButton: Boolean,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit,
    onVoiceClick: () -> Unit,
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
                AttachmentButton(onClick = onAttachClick)

                Crossfade(
                    targetState = showSendButton,
                    animationSpec = tween(durationMillis = 300),
                    modifier = Modifier.size(INPUT_ICON_SIZE.dp)
                ) { isSendButtonVisible ->
                    if (isSendButtonVisible) {
                        SendButton(onClick = onSendClick)
                    } else {
                        VoiceRecordButton(
                            isRecording = isRecording,
                            onClick = onVoiceClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Text field for entering messages
 */
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
        )
        ,
        placeholder = {
            Text(
                text = "Type a message...",
                color = MaterialTheme.colorScheme.outline,
                fontSize = MESSAGE_INPUT_TEXT_SIZE.sp
            )
        }
    )
}

/**
 * Button for attaching media files
 */
@Composable
private fun AttachmentButton(onClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .size(INPUT_ICON_SIZE.dp)
            .clickable(onClick = onClick),
        painter = painterResource(R.drawable.ic_attach_file),
        tint = MaterialTheme.colorScheme.onPrimary,
        contentDescription = "Attach files"
    )
}

@Composable
private fun SendButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
    ) {
        Icon(
            modifier = Modifier.size(INPUT_ICON_SIZE.dp),
            painter = painterResource(id = R.drawable.ic_send),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Send message"
        )
    }
}

@Composable
fun VoiceRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isRecording) 1.5f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "ScaleAnimation"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false)  // Visual feedback
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .scale(scale)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    )
            )
        }

        Icon(
            modifier = Modifier.size(INPUT_ICON_SIZE.dp),
            painter = painterResource(R.drawable.ic_voice),
            contentDescription = if (isRecording) "Stop recording" else "Start recording",
            tint = if (isRecording) {
                MaterialTheme.colorScheme.onTertiaryContainer
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    }
}