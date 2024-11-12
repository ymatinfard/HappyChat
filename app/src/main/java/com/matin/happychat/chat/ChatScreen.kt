package com.matin.happychat.chat

import MediaUtils
import MessageTimeStamp
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.matin.happychat.R
import com.matin.happychat.designsystem.HappyChatIcons
import com.matin.happychat.mediaplayer.HappyChatMediaPlayer
import com.matin.happychat.mediaplayer.HappyChatMediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    player: HappyChatMediaPlayer,
    mediaRecorder: HappyChatMediaRecorder
) {

    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ChatTopBar(scrollBehavior = scrollBehavior)
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
    ) { innerPadding ->
        val messages by viewModel.uiState.map { it.messages }
            .collectAsState(initial = emptyList())

        val currentMessage by viewModel.uiState.map { it.currentMessage }
            .collectAsState(initial = "")

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MessageList(modifier = Modifier.weight(1f), messages, listState, player)
            MessageInput(
                message = currentMessage,
                onMessageTextChange = viewModel::onUpdateMessage,
                onSendTextMessage = viewModel::onSendMessage,
                onSendPhotoMessage = viewModel::onSendImageMessage,
                onSendVoiceMessage = viewModel::onSendVoiceMessage,
                mediaRecorder = mediaRecorder,
                coroutineScope = coroutineScope,
                listState = listState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatTopBar(scrollBehavior: TopAppBarScrollBehavior? = null) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        scrollBehavior = scrollBehavior,
        title = {
            Column {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("USF")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(" ")
                            append(stringResource(R.string.online))
                        }
                    },
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }, actions = {
            Row(modifier = Modifier.padding(end = 8.dp)) {
                Icon(
                    imageVector = HappyChatIcons.SEARCH,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = HappyChatIcons.INFO,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        navigationIcon = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )

                Image(
                    painter = painterResource(R.drawable.profile_img),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(42.dp),
                    contentDescription = null
                )
            }
        })
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MessageInput(
    message: String,
    onMessageTextChange: (String) -> Unit,
    onSendTextMessage: (String) -> Unit,
    onSendPhotoMessage: (String) -> Unit,
    onSendVoiceMessage: (String) -> Unit,
    mediaRecorder: HappyChatMediaRecorder,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    modifier: Modifier,
) {

    val context = LocalContext.current
    var showPhotoPicker by remember {
        mutableStateOf(false)
    }

    var isRecording by remember {
        mutableStateOf(false)
    }

    val photoPickLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                onSendPhotoMessage(uri.toString())
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
            showPhotoPicker = false
        }

    val externalStoragePermissionLauncher = permissionLauncher(denied = {
        toast(context, "Permission denied")
    }) {
        showPhotoPicker = true
    }

    val voiceRecordPermissionLauncher = permissionLauncher(denied = {
        toast(context, "Permission denied")
    }) {
        isRecording = true
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
        keyboardController?.hide()
    }

    if (showPhotoPicker) {
        if (isPermissionGranted(
                context = context,
                permission = Manifest.permission.READ_MEDIA_IMAGES
            )
        ) {
            photoPickLauncher.launch("image/*")
        } else {
            externalStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    Surface {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                value = message,
                onValueChange = {
                    onMessageTextChange(it)
                },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            showPhotoPicker = true
                        },
                    painter = painterResource(R.drawable.ic_attach_file),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null
                )

                if (message.isNotBlank()) {
                    SendMessageButton(onSendTextMessage, message, coroutineScope, listState)
                } else {
                    VoiceRecordingIcon(isRecording) {
                        isRecording = if (isRecording) {
                            mediaRecorder.stopRecording(onSendVoiceMessage)
                            false
                        } else {
                            true
                        }
                    }
                }
            }
        }

        if (isRecording) {
            if (isPermissionGranted(
                    context = context,
                    permission = Manifest.permission.RECORD_AUDIO
                )
            ) {
                mediaRecorder.startRecording()
            } else {
                isRecording = false
                LaunchedEffect(Unit) {
                    voiceRecordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }
}


private fun toast(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
private fun SendMessageButton(
    onSendTextMessage: (String) -> Unit,
    message: String,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
) {
    IconButton(
        onClick = {
            onSendTextMessage(message)
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        },
        modifier = Modifier
            .clip(CircleShape)
            .minimumInteractiveComponentSize(),
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = R.drawable.ic_send),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null
        )
    }
}

@Composable
private fun VoiceRecordingIcon(isRecording: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "Recording transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = "ScaleAnimation"
    )

    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
    ) {
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .scale(scale)
                    .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape)
            )
        }
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.ic_voice),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
        )
    }
}

private fun isPermissionGranted(permission: String, context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun MessageList(
    modifier: Modifier,
    messages: List<Message>,
    listState: LazyListState,
    player: HappyChatMediaPlayer,
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = listState
        ) {
            items(messages, key = {
                it.baseMessage.timeStamp
            }) { message ->
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    contentAlignment = if (message.baseMessage.author == "me") Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier
                            .clip(shape = RoundedCornerShape(8.dp))
                            .background(color = if (message.baseMessage.author == "me") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                            .padding(
                                start = 6.dp,
                                end = 6.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            )
                    ) {
                        when (message) {
                            is TextMessage -> {
                                Column(verticalArrangement = Arrangement.Bottom) {
                                    Text(
                                        text = message.baseMessage.message,
                                        color = chooseOnSurfaceColorFor(message.baseMessage.author),
                                        fontSize = 18.sp,
                                    )
                                    MessageTimeStamp(
                                        timeStamp = message.baseMessage.timeStamp,
                                        modifier = Modifier.align(alignment = Alignment.End)
                                    )
                                }
                            }

                            is ImageMessage -> {
                                ImageMessageBubble(message.imageUri)
                            }

                            is VoiceMessage -> {
                                VoiceMessageBubble(message, player)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun chooseOnSurfaceColorFor(author: String) =
    if (author == "me") MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary

@Composable
fun ImageMessageBubble(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_happy_chat),
        contentDescription = "Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(width = 200.dp, height = 300.dp),
    )
}


@Composable
fun VoiceMessageBubble(message: Message, player: HappyChatMediaPlayer) {
    val voiceMessage = message as VoiceMessage
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    val duration by remember { mutableLongStateOf(MediaUtils.getDuration(voiceMessage.voicePath)) }
    var remainingTime by rememberSaveable { mutableLongStateOf(duration) }

    val playBackStateListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_ENDED,
                ExoPlayer.STATE_IDLE,
                    -> {
                    isPlaying = false
                    remainingTime = duration
                }
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && player.currentPosition < duration) {
            remainingTime = duration - player.currentPosition
            delay(300)
        }
    }

    Box(
        modifier = Modifier
            .width(300.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    when {
                        isPlaying -> {
                            player.pause()
                            isPlaying = false
                        }

                        player.isPaused(message.voicePath) -> {
                            player.play()
                        }

                        else -> {
                            player.startPlayer(message.voicePath, playBackStateListener)
                        }
                    }
                    isPlaying = !isPlaying
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_stop_media else R.drawable.ic_play_media
                        ),
                        modifier = Modifier.size(42.dp),
                        tint = chooseOnSurfaceColorFor(author = message.baseMessage.author),
                        contentDescription = null
                    )
                }

                LinearProgressIndicator(
                    progress = duration - remainingTime.toFloat(),
                    modifier = Modifier
                        .padding(start = 4.dp, end = 6.dp)
                        .weight(1f)
                        .background(color = MaterialTheme.colorScheme.onBackground),
                    color = chooseOnSurfaceColorFor(author = message.baseMessage.author)
                )

                Text(
                    text = remainingTime.withFormat("%02d:%02d"),
                    fontSize = 14.sp,
                    color = chooseOnSurfaceColorFor(author = message.baseMessage.author)
                )
            }
            MessageTimeStamp(
                timeStamp = message.baseMessage.timeStamp,
                modifier = Modifier.align(alignment = Alignment.End)
            )
        }
    }
}

fun Long.withFormat(format: String): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(format, minutes, seconds)
}

@Composable
fun permissionLauncher(denied: () -> Unit = {}, granted: () -> Unit) =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            granted()
        } else {
            denied()
        }
    }

const val DISABLED_ALPHA = 0.38F
