package com.matin.happychat.chat

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import com.matin.happychat.designsystem.component.ChatTopBar
import com.matin.happychat.designsystem.component.MediaPickerLauncher
import com.matin.happychat.designsystem.component.MessageInputBar
import com.matin.happychat.designsystem.component.MessageList
import com.matin.happychat.designsystem.component.PermissionRequestHandler
import com.matin.happychat.mediaplayer.VoiceMessagePlayer
import com.matin.happychat.mediaplayer.VoiceMessageRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    voiceMessagePlayer: VoiceMessagePlayer,
    voiceMessageRecorder: VoiceMessageRecorder,
    onNavigateBack: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val uiState by viewModel.uiState.collectAsState()

    val shouldShowSendButton by remember(uiState.currentMessage) {
        derivedStateOf { uiState.currentMessage.isNotBlank() }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ChatTopBar(
                scrollBehavior = scrollBehavior,
                onBackClick = onNavigateBack,
                onInfoClick = viewModel::onInfoClick,
                onSearchClick = viewModel::onSearchClick
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MessageList(
                modifier = Modifier.weight(1f),
                messages = uiState.messages,
                listState = listState,
                playerController = voiceMessagePlayer,
                onMessageClick = viewModel::onMessageClick
            )

            MessageInputBar(
                message = uiState.currentMessage,
                isRecording = uiState.isRecording,
                showSendButton = shouldShowSendButton,
                onMessageChange = viewModel::onUpdateMessage,
                onSendClick = { sendTextMessage(viewModel, coroutineScope, listState) },
                onAttachClick = {
                    requestMediaPermission(
                        viewModel,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                },
                onVoiceClick = { handleVoiceRecordingAction(viewModel, voiceMessageRecorder) },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    MediaPickerLauncher(
        showPicker = uiState.isShowingPhotoPicker,
        onMediaSelected = { uri ->
            viewModel.onSendImageMessage(uri.toString())
            scrollToBottom(coroutineScope, listState)
        },
        onDismiss = viewModel::dismissPhotoPicker
    )

    PermissionRequestHandler(
        permissionsToRequest = uiState.pendingPermissions,
        onPermissionResult = viewModel::onPermissionResult,
    )
}

private fun sendTextMessage(
    viewModel: ChatViewModel,
    coroutineScope: CoroutineScope,
    listState: LazyListState
) {
    viewModel.onEvent(event = ChatEvent.SendMessage)
    scrollToBottom(coroutineScope, listState)
}

private fun scrollToBottom(coroutineScope: CoroutineScope, listState: LazyListState) {
    coroutineScope.launch {
        listState.animateScrollToItem(0)
    }
}

private fun handleVoiceRecordingAction(
    viewModel: ChatViewModel,
    voiceMessageRecorder: VoiceMessageRecorder
) {
    if (viewModel.uiState.value.isRecording) {
        val filePath = voiceMessageRecorder.stopRecording()
        //  viewModel.onSendVoiceMessage(filePath)
        viewModel.setRecordingState(false)
    } else {
        // Check permission if granted then start recording
        viewModel.requestPermission(Manifest.permission.RECORD_AUDIO)
    }
}

private fun requestMediaPermission(viewModel: ChatViewModel, permission: String) {
    viewModel.requestPermission(permission)
}
