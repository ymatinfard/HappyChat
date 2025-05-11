package com.matin.happychat.chat

import android.net.Uri
import com.matin.happychat.chat.Message.Companion.CURRENT_USER_ID
import java.time.Instant

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE
}

/**
 * Base message interface with common properties
 */
interface Message {
    val id: Long
    val content: String
    val author: String
    val timestamp: Long
    val type: MessageType
    val isFromCurrentUser: Boolean
        get() = author == CURRENT_USER_ID

    companion object {
        const val CURRENT_USER_ID = "me"
    }
}

abstract class BaseMessage(
    override val id: Long = Instant.now().toEpochMilli(),
    override val content: String = "",
    override val author: String = CURRENT_USER_ID,
    override val timestamp: Long = System.currentTimeMillis(),
) : Message

data class TextMessage(
    override val id: Long = Instant.now().toEpochMilli(),
    override val content: String,
    override val author: String = CURRENT_USER_ID,
    override val timestamp: Long = System.currentTimeMillis(),
) : BaseMessage(id, content, author, timestamp) {
    override val type: MessageType = MessageType.TEXT
}

data class ImageMessage(
    override val id: Long = Instant.now().toEpochMilli(),
    override val content: String = "", // Optional caption
    override val author: String = CURRENT_USER_ID,
    override val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String,
    val width: Int? = null,
    val height: Int? = null,
) : BaseMessage(id, content, author, timestamp) {
    override val type: MessageType = MessageType.IMAGE
}

data class VoiceMessage(
    override val id: Long = Instant.now().toEpochMilli(),
    override val content: String = "", // Optional transcription
    override val author: String = CURRENT_USER_ID,
    override val timestamp: Long = System.currentTimeMillis(),
    val voicePath: Uri,
    val durationMs: Long,
) : BaseMessage(id, content, author, timestamp) {
    override val type: MessageType = MessageType.VOICE
}

/**
 * Factory methods to create messages
 */
object MessageFactory {
    fun createTextMessage(
        content: String,
        author: String = Message.CURRENT_USER_ID
    ): TextMessage {
        return TextMessage(
            content = content,
            author = author
        )
    }

    fun createImageMessage(
        imageUri: String,
        caption: String = "",
        author: String = Message.CURRENT_USER_ID
    ): ImageMessage {
        return ImageMessage(
            content = caption,
            imageUri = imageUri,
            author = author
        )
    }

    fun createVoiceMessage(
        voicePath: Uri,
        durationMs: Long,
        transcription: String = "",
        author: String = Message.CURRENT_USER_ID
    ): VoiceMessage {
        return VoiceMessage(
            content = transcription,
            voicePath = voicePath,
            durationMs = durationMs,
            author = author
        )
    }
}