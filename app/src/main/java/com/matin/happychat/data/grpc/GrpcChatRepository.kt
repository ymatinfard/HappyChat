package com.matin.happychat.data.grpc

import com.matin.happychat.ChatMessage
import com.matin.happychat.domain.Message
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface GrpcChatRepository {
    fun sendTextMessage(message: Message)
    fun observeTextMessages(): SharedFlow<ChatMessage>
}

class GrpcGrpcChatRepositoryImpl @Inject constructor(private val grpcClient: GRPCClient) : GrpcChatRepository {

    private val chatSession = grpcClient.createChatSession()

    override fun observeTextMessages(): SharedFlow<ChatMessage> = chatSession.messageFlow

    override fun sendTextMessage(message: Message) {
//        chatSession.sendMessage(
//            message.baseMessage.author,
//            "receiveId",
//            message.baseMessage.message
//        )
    }
}