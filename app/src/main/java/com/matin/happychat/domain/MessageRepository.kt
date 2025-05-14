package com.matin.happychat.domain

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getMessages(): Flow<List<Message>>
    suspend fun insertToDb(text: String)
}