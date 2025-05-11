package com.matin.happychat.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matin.happychat.data.grpc.GrpcChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: GrpcChatRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // Fake data to represent received messages
        _uiState.update { state ->
            state.copy(
                messages = fakeReceivedMessages
            )
        }

        viewModelScope.launch {
            repository.observeTextMessages().collect {
                Log.d("HappyChat", "Messages: " + it.message)
            }
        }
    }

    fun onSendMessage(message: String) {
        if (message.trim().isEmpty()) return
        val newMessage =
            TextMessage(
                BaseMessage(
                    message = message,
                    author = "me",
                    timeStamp = System.currentTimeMillis()
                )
            )
        _uiState.update { state ->
            repository.sendTextMessage(newMessage)
            updateChatUiState(state, newMessage)
        }
    }

    fun onUpdateMessage(newMessage: String) {
        _uiState.update { state ->
            state.copy(currentMessage = newMessage)
        }
    }

    fun onSendImageMessage(selectedPhotoUri: String) {
        val newMessage = ImageMessage(
            baseMessage = BaseMessage("Photo", "me", System.currentTimeMillis()),
            imageUri = selectedPhotoUri
        )

        _uiState.update { state ->
            updateChatUiState(state, newMessage)
        }
    }

    fun onSendVoiceMessage(path: String) {
        val newMessage = VoiceMessage(
            voicePath = path,
            baseMessage = BaseMessage("My voice", "me", System.currentTimeMillis())
        )
        _uiState.update { state ->
            updateChatUiState(state, newMessage)
        }
    }

    private fun updateChatUiState(
        state: ChatUiState,
        newMessage: Message,
    ): ChatUiState {
        val oldMessages = state.messages
        return state.copy(messages = oldMessages.add(newMessage), currentMessage = "")
    }
}

fun List<Message>.add(message: Message) =
    this.toMutableList().apply { add(index = 0, element = message) }

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
)

val fakeReceivedMessages = listOf(
    TextMessage(
        baseMessage = BaseMessage(
            message = "How are you?",
            author = "you",
            timeStamp = System.currentTimeMillis() + 123
        )
    ),
    TextMessage(
        baseMessage = BaseMessage(
            message = "Hi",
            author = "you",
            timeStamp = System.currentTimeMillis()
        )
    ),
)
