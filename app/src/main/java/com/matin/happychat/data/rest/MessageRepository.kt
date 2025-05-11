package com.matin.happychat.data.rest

import com.matin.happychat.chat.Message
import com.matin.happychat.chat.MessageFactory.createTextMessage
import javax.inject.Inject

interface MessageRepository {
    suspend fun getMessages(): List<Message>
    suspend fun sendTextMessage(text: String)
    suspend fun sendImageMessage(uri: String)
    suspend fun sendVoiceMessage(path: String)
}

class MessageRepositoryImpl @Inject constructor(): MessageRepository {
    val messages = mutableListOf<Message>()
    override suspend fun getMessages(): List<Message> {
        return messages
    }

    override suspend fun sendTextMessage(text: String) {
        messages.add(createTextMessage(text))
    }

    override suspend fun sendImageMessage(uri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sendVoiceMessage(path: String) {
        TODO("Not yet implemented")
    }
}