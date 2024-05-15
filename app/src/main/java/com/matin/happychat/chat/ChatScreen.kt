package com.matin.happychat.chat

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.matin.happychat.R
import com.matin.happychat.designsystem.HappyChatIcons
import com.matin.happychat.designsystem.theme.HappyChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {

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
            MessageList(modifier = Modifier.weight(1f), messages, listState)
            MessageInput(
                message = currentMessage,
                onMessageTextChange = viewModel::onUpdateMessage,
                onSendMessageClick = viewModel::onSendMessage,
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
    CenterAlignedTopAppBar(modifier =
    Modifier
        .padding(start = 8.dp, end = 8.dp),
        scrollBehavior = scrollBehavior,
        title = {
            Column {
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(R.string.happy))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.DarkGray
                        )
                    ) {
                        append(" ")
                        append(stringResource(R.string.online))
                    }
                })
            }
        }, actions = {
            Row {
                Icon(
                    imageVector = HappyChatIcons.SEARCH,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = HappyChatIcons.INFO,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified,
                painter = painterResource(id = R.drawable.ic_happy_chat),
                contentDescription = null
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MessageInput(
    message: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessageClick: (String) -> Unit,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    modifier: Modifier,
) {

    var showPhotoPicker by remember {
        mutableStateOf(false)
    }

    val photoPickLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            showPhotoPicker = false
        }

    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(start = 8.dp, end = 8.dp)
        ) {

            TextField(
                modifier = Modifier.weight(1f),
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

            Row {
                Icon(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clickable {
                            // readExternalStoragePermissionGranted =
                            showPhotoPicker = true
                        },
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        onSendMessageClick(message)
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = LocalContentColor.current.copy(alpha = DISABLED_ALPHA)
                    )
                ) {
                    Text(text = "Send")
                }
            }

        }

        if (showPhotoPicker) {
            PhotoPicker(photoPickLauncher) {
                showPhotoPicker = false
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoPicker(
    photoPickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    onDismiss: () -> Unit,
) {
    val readStoragePermission =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted.not()) {
            //
            }
        }

    LaunchedEffect(key1 = readStoragePermission) {
        when {
            readStoragePermission.status.isGranted -> {
                photoPickerLauncher.launch("image/*")
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    onDismiss()
}

@Composable
fun MessageList(modifier: Modifier, messages: List<Message>, listState: LazyListState) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = listState
        ) {
            items(messages, key = {
                it.timeStamp
            }) { message ->
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    contentAlignment = if (message.author == "me") Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier
                            .clip(shape = RoundedCornerShape(8.dp))
                            .background(color = if (message.author == "me") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                            .padding(
                                start = 6.dp,
                                end = 6.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            )
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = message.message,
                                color = if (message.author == "me") MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary,
                                fontSize = 18.sp,
                            )
                            Text(
                                text = formatTime(message.timeStamp),
                                color = Color.DarkGray,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MessagePreview(modifier: Modifier = Modifier) {
    HappyChatTheme {
        MessageList(
            modifier = Modifier,
            messages = listOf(
                Message(
                    message = "Hello",
                    author = "me",
                    timeStamp = System.currentTimeMillis()
                )
            ),
            listState = LazyListState()
        )
    }
}

fun formatTime(timeStamp: Long) = SimpleDateFormat("HH:mm a").format(timeStamp)

const val DISABLED_ALPHA = 0.38F
