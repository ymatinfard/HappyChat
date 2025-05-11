package com.matin.happychat.designsystem.component

import MediaUtils
import MessageTimeStamp
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.matin.happychat.R
import com.matin.happychat.chat.ImageMessage
import com.matin.happychat.chat.Message
import com.matin.happychat.chat.TextMessage
import com.matin.happychat.chat.VoiceMessage
import com.matin.happychat.mediaplayer.VoiceMessagePlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Constants for UI dimensions and animations
private const val MESSAGE_BUBBLE_CORNER_RADIUS = 8
private const val IMAGE_MESSAGE_WIDTH = 200
private const val IMAGE_MESSAGE_HEIGHT = 300
private const val VOICE_MESSAGE_WIDTH = 300
private const val VOICE_PLAYER_ICON_SIZE = 42
private const val VOICE_PLAYBACK_UPDATE_INTERVAL = 300L
private const val MESSAGE_TEXT_SIZE = 18
internal const val TIMESTAMP_TEXT_SIZE = 14

/**
 * Displays the list of messages
 */
@Composable
fun MessageList(
    modifier: Modifier,
    messages: List<Message>,
    listState: LazyListState,
    playerController: VoiceMessagePlayer,
    onMessageClick: (Long) -> Unit
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = listState
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                MessageItem(
                    message = message,
                    playerController = playerController,
                    onMessageClick = { onMessageClick(message.id) }
                )
            }
        }
    }
}

/**
 * A single message item in the list
 */
@Composable
private fun MessageItem(
    message: Message,
    playerController: VoiceMessagePlayer,
    onMessageClick: () -> Unit
) {
    val alignment = if (message.isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isFromCurrentUser)
        MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(MESSAGE_BUBBLE_CORNER_RADIUS.dp))
                .background(color = backgroundColor)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            MessageContent(
                message = message,
                playerController = playerController,
                onMessageClick = onMessageClick
            )
        }
    }
}

/**
 * Renders appropriate content based on message type
 */
@Composable
private fun MessageContent(
    message: Message,
    playerController: VoiceMessagePlayer,
    onMessageClick: () -> Unit
) {
    when (message) {
        is TextMessage -> TextMessageContent(message)
        is ImageMessage -> ImageMessageContent(message.imageUri)
        is VoiceMessage -> VoiceMessageContent(message, playerController)
    }
}

@Composable
private fun TextMessageContent(message: TextMessage) {
    Column(verticalArrangement = Arrangement.Bottom) {
        Text(
            text = message.content,
            color = chooseOnSurfaceColorFor(message.isFromCurrentUser),
            fontSize = MESSAGE_TEXT_SIZE.sp,
        )
        MessageTimeStamp(
            timeStamp = message.timestamp,
            isFromCurrentUser = message.isFromCurrentUser,
            modifier = Modifier.align(alignment = Alignment.End)
        )
    }
}

@Composable
private fun ImageMessageContent(imageUri: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_happy_chat),
        contentDescription = "Image Message",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(
            width = IMAGE_MESSAGE_WIDTH.dp,
            height = IMAGE_MESSAGE_HEIGHT.dp
        ),
    )
}

@Composable
private fun VoiceMessageContent(
    message: VoiceMessage,
    playerController: VoiceMessagePlayer
) {
    val voicePath = message.voicePath
    var isPlaying by remember { mutableStateOf(false) }
    val duration by remember { mutableLongStateOf(MediaUtils.getDuration(voicePath.toString())) }
    var progress by remember { mutableLongStateOf(0L) }

    // Create playback progress flow
    val playbackProgressFlow = remember(isPlaying, voicePath) {
        createPlaybackProgressFlow(
            isPlaying = isPlaying,
            playerController = playerController,
            voicePath = voicePath
        )
    }

    // Observe playback progress
    LaunchedEffect(playbackProgressFlow) {
        playbackProgressFlow.collect { currentProgress ->
            progress = currentProgress
        }
    }

    // Handle player state changes
//    DisposableEffect(voicePath) {
//        val listener = object : MediaPlayerController.PlayerStateListener {
//            override fun onStateChanged(newState: MediaPlayerController.PlayerState) {
//                isPlaying = newState == MediaPlayerController.PlayerState.PLAYING
//                if (newState == MediaPlayerController.PlayerState.COMPLETED) {
//                    progress = 0L
//                }
//            }
//        }
//
//        playerController.addStateListener(voicePath, listener)
//
//        onDispose {
//            playerController.removeStateListener(voicePath, listener)
//        }
//    }

    Box(
        modifier = Modifier
            .width(VOICE_MESSAGE_WIDTH.dp)
            .clip(RoundedCornerShape(MESSAGE_BUBBLE_CORNER_RADIUS.dp))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (isPlaying) {
                        playerController.pause()
                    } else {
                        playerController.play(voicePath, listener = object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                isPlaying = playbackState == Player.STATE_READY
                            }
                        })
                    }
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying)
                                R.drawable.ic_stop_media
                            else
                                R.drawable.ic_play_media
                        ),
                        modifier = Modifier.size(VOICE_PLAYER_ICON_SIZE.dp),
                        tint = chooseOnSurfaceColorFor(message.isFromCurrentUser),
                        contentDescription = if (isPlaying) "Stop" else "Play"
                    )
                }

                LinearProgressIndicator(
                    progress = if (duration > 0) progress.toFloat() / duration else 0f,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 6.dp)
                        .weight(1f)
                        .background(color = MaterialTheme.colorScheme.onBackground),
                    color = chooseOnSurfaceColorFor(message.isFromCurrentUser)
                )

                Text(
                    text = formatDuration(duration - progress),
                    fontSize = TIMESTAMP_TEXT_SIZE.sp,
                    color = chooseOnSurfaceColorFor(message.isFromCurrentUser)
                )
            }

            MessageTimeStamp(
                timeStamp = message.timestamp,
                isFromCurrentUser = message.isFromCurrentUser,
                modifier = Modifier.align(alignment = Alignment.End)
            )
        }
    }
}

/**
 * Creates a flow that emits playback progress updates
 */
private fun createPlaybackProgressFlow(
    isPlaying: Boolean,
    playerController: VoiceMessagePlayer,
    voicePath: Uri
): Flow<Long> = flow {
    while (isPlaying) {
        emit(playerController.currentPosition)
        delay(VOICE_PLAYBACK_UPDATE_INTERVAL)
    }
}

/**
 * Format duration in MM:SS format
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}