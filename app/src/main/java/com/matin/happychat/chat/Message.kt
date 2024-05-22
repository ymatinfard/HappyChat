package com.matin.happychat.chat

data class BaseMessage(
    val message: String = "",
    val author: String = "me",
    val timeStamp: Long = 0,
)

interface Message {
    val baseMessage: BaseMessage
}

data class TextMessage(override val baseMessage: BaseMessage = BaseMessage()) : Message
data class ImageMessage(
    override val baseMessage: BaseMessage = BaseMessage(),
    val imageUri: String,
) : Message
data class VoiceMessage(override val baseMessage: BaseMessage = BaseMessage(), val voicePath: String): Message