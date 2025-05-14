package com.matin.happychat.chat

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matin.happychat.domain.Message
import com.matin.happychat.domain.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            messageRepository.getMessages()
                .catch { e ->
                    _uiState.update { currentState ->
                        currentState.copy(messages = emptyList())
                    }
                }
                .collect { messages ->
                    _uiState.update { currentState ->
                        currentState.copy(messages = messages)
                    }
                }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.UpdateMessage -> onUpdateMessage(event.text)
            is ChatEvent.SendMessage -> onSendMessage()
            is ChatEvent.SendImageMessage -> onSendImageMessage(event.uri)
            is ChatEvent.SendVoiceMessage -> onSendVoiceMessage(event.path)
            is ChatEvent.RequestPermission -> requestPermission(event.permission)
            is ChatEvent.PermissionResult -> onPermissionResult(event.permissions)
            is ChatEvent.DismissPhotoPicker -> dismissPhotoPicker()
            is ChatEvent.SearchClick -> onSearchClick()
            is ChatEvent.InfoClick -> onInfoClick()
            is ChatEvent.MessageClick -> onMessageClick(event.messageId)
        }
    }

    fun onUpdateMessage(text: String) {
        _uiState.update { it.copy(currentMessage = text) }
    }

    private fun onSendMessage() {
        val currentText = _uiState.value.currentMessage.trim()
        if (currentText.isNotBlank()) {
            viewModelScope.launch {
                messageRepository.insertToDb(currentText)
                // Clear input field after sending
                _uiState.update {
                    it.copy(
                        currentMessage = "",
                    )
                }
            }
        }
    }

    fun onSendImageMessage(uri: String) {

    }

    fun onSendVoiceMessage(path: String) {

    }

    fun requestPermission(permission: String) {
        _uiState.update {
            it.copy(
                pendingPermissions = it.pendingPermissions + permission
            )
        }
    }

    fun onPermissionResult(permissions: Map<String, Boolean>) {
        val newPermissions = _uiState.value.pendingPermissions - permissions.keys

        _uiState.update {
            it.copy(pendingPermissions = newPermissions)
        }

        permissions.forEach { (permission, isGranted) ->
            if (!isGranted) return@forEach
            when (permission) {
                Manifest.permission.READ_MEDIA_IMAGES -> {
                    _uiState
                        .update { it.copy(isShowingPhotoPicker = true) }
                }

                Manifest.permission.RECORD_AUDIO -> {
                    _uiState.update { it.copy(isRecording = true) }
                }
            }
        }
    }

    fun dismissPhotoPicker() {
        _uiState.update { it.copy(isShowingPhotoPicker = false) }
    }

    fun setRecordingState(isRecording: Boolean) {
        _uiState.update { it.copy(isRecording = isRecording) }
    }

    fun onSearchClick() {
    }

    fun onInfoClick() {
    }

    fun onMessageClick(messageId: String) {
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isRecording: Boolean = false,
    val isShowingPhotoPicker: Boolean = false,
    val pendingPermissions: Set<String> = emptySet()
)

sealed class ChatEvent {
    data class UpdateMessage(val text: String) : ChatEvent()
    object SendMessage : ChatEvent()
    data class SendImageMessage(val uri: String) : ChatEvent()
    data class SendVoiceMessage(val path: String) : ChatEvent()
    data class RequestPermission(val permission: String) : ChatEvent()
    data class PermissionResult(val permissions: Map<String, Boolean>) : ChatEvent()
    object DismissPhotoPicker : ChatEvent()
    object SearchClick : ChatEvent()
    object InfoClick : ChatEvent()
    data class MessageClick(val messageId: String) : ChatEvent()
}
