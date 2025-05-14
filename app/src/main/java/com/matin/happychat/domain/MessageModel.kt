package com.matin.happychat.domain

import android.net.Uri
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.common.model.MessageType
import com.matin.happychat.data.model.MessageEntity
import com.matin.happychat.data.model.MessageNetwork
import java.time.Instant
import java.util.UUID

const val CURRENT_USER_ID = "me"

abstract class BaseMessage(
    open val id: Long = UUID.randomUUID().timestamp(),
    open val content: String = "",
    open val author: String = CURRENT_USER_ID,
    open val createdAt: Long = Instant.now().toEpochMilli(),
    open val state: MessageState,
) {
    abstract val type: MessageType
    val isFromCurrentUser: Boolean = (author == CURRENT_USER_ID)
}

data class Message(
    override val id: Long = UUID.randomUUID().timestamp(),
    override val content: String,
    override val author: String = CURRENT_USER_ID,
    override val createdAt: Long = Instant.now().toEpochMilli(),
    override val state: MessageState = MessageState.PENDING,
) : BaseMessage(id, content, author, createdAt, state) {
    override val type: MessageType = MessageType.TEXT
}

data class ImageMessage(
    override val id: Long = UUID.randomUUID().timestamp(),
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
    override val id: Long = UUID.randomUUID().timestamp(),
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

fun Message.toNetwork(): MessageNetwork {
    return MessageNetwork(
        id = id,
        content = content,
        author = author,
        createdAt = createdAt,
    )
}

fun MessageNetwork.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        content = content,
        author = author,
        type = MessageType.TEXT,
        state = MessageState.SENT,
        timestamp = createdAt
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

fun MessageEntity.toNetwork(): MessageNetwork {
    return MessageNetwork(
        id = id,
        content = content,
        author = author,
        createdAt = timestamp,
    )
}