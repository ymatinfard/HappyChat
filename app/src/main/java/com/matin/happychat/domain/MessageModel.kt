package com.matin.happychat.domain

import android.net.Uri
import com.matin.happychat.common.model.ChatIdGenerator
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.common.model.MessageType
import com.matin.happychat.data.model.MessageEntity
import com.matin.happychat.data.model.MessageRequestNetwork
import com.matin.happychat.data.model.MessageResponseNetwork
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

fun MessageResponseNetwork.toEntity(): MessageEntity {
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

fun Message.toNetwork(): MessageRequestNetwork {
    return MessageRequestNetwork(
        sender = author,
        message = content
    )
}