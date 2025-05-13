package com.matin.happychat.domain

interface MessageRepository {
    suspend fun getMessages(): List<Message>
    suspend fun sendTextMessage(text: String)
    suspend fun sendImageMessage(uri: String)
    suspend fun sendVoiceMessage(path: String)
}