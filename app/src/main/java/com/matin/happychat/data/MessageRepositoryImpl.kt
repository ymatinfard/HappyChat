package com.matin.happychat.data

import com.matin.happychat.domain.Message
import com.matin.happychat.domain.MessageFactory.createTextMessage
import com.matin.happychat.domain.MessageRepository
import javax.inject.Inject

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