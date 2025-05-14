package com.matin.happychat.data

import android.util.Log
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.data.model.MessageRequest
import com.matin.happychat.data.rest.ChatApi
import com.matin.happychat.di.IoDispatcher
import com.matin.happychat.domain.Message
import com.matin.happychat.domain.MessageFactory.createMessage
import com.matin.happychat.domain.MessageRepository
import com.matin.happychat.domain.toDomain
import com.matin.happychat.domain.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val chatApi: ChatApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MessageRepository {

    private val externalSupervisorScope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val pendingMessages = Channel<Message>(Channel.BUFFERED)
    private val semaphore = Semaphore(5)

    init {
        externalSupervisorScope.launch {
            processPendingMessages()
        }
    }

    override suspend fun getMessages(): Flow<List<Message>> {
        return messageDao.getAllMessages().distinctUntilChanged()
            .map { entityList -> entityList.map { it.toDomain() } }
    }

    override suspend fun insertToDb(text: String) {
        try {
            val message = createMessage(text)
            withContext(ioDispatcher) {
                messageDao.insertMessageToDb(message.toEntity())
            }
            pendingMessages.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun processPendingMessages() {
        for (message in pendingMessages) {
            externalSupervisorScope.launch {
                semaphore.withPermit {
                    sendToServer(message)
                }
            }
        }
    }

    suspend fun retryFailedMessages() {
        try {
            val failedMessages: List<Message> = withContext(ioDispatcher) {
                messageDao.getFailedMessages().map { it.toDomain() }
            }

            for (message in failedMessages) {
                externalSupervisorScope.launch {
                    sendToServer(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun sendToServer(message: Message) = withContext(ioDispatcher) {
        try {
            val messageRequest = MessageRequest(
                sender = message.author,
                message = message.content
            )
            val responseMessage = chatApi.sendMessage(messageRequest)

            updateMessageState(message.id, MessageState.SENT)
            messageDao.insertMessageToDb(responseMessage.first().toEntity())
        } catch (e: Exception) {
            try {
                updateMessageState(message.id, MessageState.FAILED)
            } catch (e: Exception) {
                Log.e("MessageRepository", "Failed to update message state in DB")
            }
        }
    }

    private fun updateMessageState(messageId: String, newState: MessageState) {
        messageDao.updateMessageState(messageId, newState)
    }
}