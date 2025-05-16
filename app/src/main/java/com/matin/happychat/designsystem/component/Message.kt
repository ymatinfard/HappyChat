package com.matin.happychat.designsystem.component

import MessageTimeStamp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.designsystem.theme.HappyChatTheme
import com.matin.happychat.domain.Message

private const val MESSAGE_BUBBLE_CORNER_RADIUS = 16
private const val MESSAGE_TEXT_SIZE = 20
internal const val TIMESTAMP_TEXT_SIZE = 14

@Composable
fun MessageList(
    modifier: Modifier,
    messages: List<Message>,
    isMsgPending: Boolean,
    listState: LazyListState,
) {
    LaunchedEffect(messages.size) {
        if (listState.firstVisibleItemIndex < 3) {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = listState
        ) {
            item {
                AnimatedVisibility(visible = isMsgPending) {
                    MessageItem(isFromCurrentUser = false) {
                        LoadingPulse(
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            size = 14.dp,
                            spaceBetween = 3.dp,
                            travelDistance = 10.dp
                        )
                    }
                }
            }
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                MessageItem(message.isFromCurrentUser) {
                    MessageContent(message)
                }
            }
        }
    }
}

@Composable
private fun MessageItem(
    isFromCurrentUser: Boolean,
    content: @Composable () -> Unit,
) {
    val alignment = if (isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        MessageBubble(isFromCurrentUser) {
            content()
        }
    }
}

@Composable
private fun MessageBubble(
    isFromCurrentUser: Boolean,
    content: @Composable () -> Unit
) {
    val shape = chooseMessageBoxShape(isFromCurrentUser, MESSAGE_BUBBLE_CORNER_RADIUS.dp)
    val backgroundColor = if (isFromCurrentUser)
        MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .clip(shape = shape)
            .background(color = backgroundColor)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        content()
    }
}

@Composable
private fun chooseMessageBoxShape(
    isFromCurrentUser: Boolean,
    cornerRadius: Dp,
): RoundedCornerShape {
    val baseShape = RoundedCornerShape(cornerRadius)
    return if (isFromCurrentUser) {
        baseShape.copy(topEnd = CornerSize(0))
    } else {
        baseShape.copy(topStart = CornerSize(0))
    }
}

/**
 * Renders appropriate content based on message type
 */
@Composable
private fun MessageContent(
    message: Message,
) {
    TextMessageContent(message)
}

@Composable
private fun TextMessageContent(message: Message) {
    Column(verticalArrangement = Arrangement.Bottom) {
        Text(
            text = message.content,
            fontSize = MESSAGE_TEXT_SIZE.sp,
            color = chooseOnSurfaceColorFor(message.isFromCurrentUser)
        )
        MessageTimeStamp(
            timeStamp = message.createdAt,
            isFromCurrentUser = message.isFromCurrentUser,
            modifier = Modifier.align(alignment = Alignment.End)
        )
    }
}

@Preview
@Composable
fun MessageListPreview() {
    HappyChatTheme {
        MessageList(
            modifier = Modifier,
            messages = emptyList(),
            isMsgPending = true,
            listState = rememberLazyListState(),
        )
    }
}