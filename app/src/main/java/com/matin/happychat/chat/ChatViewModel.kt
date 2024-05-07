package com.matin.happychat.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onSendMessage(message: String) {
        val newMessage =
            Message(message = message, timeStamp = System.currentTimeMillis().toString())
        _uiState.update { state ->
            val oldMessages = state.messages
            state.copy(messages = oldMessages.add(newMessage), currentMessage = "")
        }
    }

    fun onUpdateMessage(newMessage: String) {
        _uiState.update { state ->
            state.copy(currentMessage = newMessage)
        }
    }
}

fun List<Message>.add(message: Message) = this.toMutableList().apply { add(message) }

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
)
