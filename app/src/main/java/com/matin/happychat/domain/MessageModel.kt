package com.matin.happychat.domain

import android.net.Uri
import com.matin.happychat.common.model.ChatIdGenerator
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.common.model.MessageType
import com.matin.happychat.data.model.MessageEntity
import com.matin.happychat.data.model.MessageNetworkResponse
import java.time.Instant

const val CURRENT_USER_ID = "me"
const val SERVER_USER_ID = "server"

abstract class BaseMessage(
    open val id: String = ChatIdGenerator.nextId(),
    open val content: String = "",
    open val author: String = CURRENT_USER_ID,
    open val createdAt: Long = Instant.now().toEpochMilli(),
    open val state: MessageState,
) {
    abstract val type: MessageType
    val isFromCurrentUser: Boolean
        get() = author == CURRENT_USER_ID
}

data class Message(
    override val id: String = ChatIdGenerator.nextId(),
    override val content: String,
    override val author: String = CURRENT_USER_ID,
    override val createdAt: Long = Instant.now().toEpochMilli(),
    override val state: MessageState = MessageState.PENDING,
) : BaseMessage(id, content, author, createdAt, state) {
    override val type: MessageType = MessageType.TEXT
}

data class ImageMessage(
    override val id: String = ChatIdGenerator.nextId(),
    override val content: String = "", // Optional caption
    override val author: String = CURRENT_USER_ID,
    override val createdAt: Long = Instant.now().toEpochMilli(),
    override val state: MessageState = MessageState.PENDING,
    val imageUri: String,
    val width: Int? = null,
    val height: Int? = null,
) : BaseMessage(id, content, author, createdAt, state) {
    override val type: MessageType = MessageType.IMAGE
}

data class VoiceMessage(
    override val id: String = ChatIdGenerator.nextId(),
    override val content: String = "", // Optional transcription
    override val author: String = CURRENT_USER_ID,
    override val createdAt: Long = Instant.now().toEpochMilli(),
    override val state: MessageState = MessageState.PENDING,
    val voicePath: Uri,
    val durationMs: Long,
) : BaseMessage(id, content, author, createdAt, state) {
    override val type: MessageType = MessageType.VOICE
}


/**
 * Factory methods to create messages
 */
object MessageFactory {
    fun createMessage(
        content: String,
        author: String = CURRENT_USER_ID
    ): Message {
        return Message(
            content = content,
            author = author,
        )
    }
}

//    fun createImageMessage(
//        imageUri: String,
//        caption: String = "",
//        author: String = CURRENT_USER_ID
//    ): ImageMessage {
//        return ImageMessage(
//            content = caption,
//            imageUri = imageUri,
//            author = author
//        )
//    }
//
//    fun createVoiceMessage(
//        voicePath: Uri,
//        durationMs: Long,
//        transcription: String = "",
//        author: String = CURRENT_USER_ID
//    ): VoiceMessage {
//        return VoiceMessage(
//            content = transcription,
//            voicePath = voicePath,
//            durationMs = durationMs,
//            author = author
//        )
//    }


fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        content = content,
        author = author,
        timestamp = createdAt,
        type = type,
        state = state
    )
}

fun MessageNetworkResponse.toEntity(): MessageEntity {
    return MessageEntity(
        id = ChatIdGenerator.nextId(),
        content = text,
        author = SERVER_USER_ID,
        timestamp = Instant.now().toEpochMilli(),
        type = MessageType.TEXT,
        state = MessageState.SENT
    )
}

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        content = content,
        author = author,
        createdAt = timestamp,
    )
}