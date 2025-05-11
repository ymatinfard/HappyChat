package com.matin.happychat.data.grpc

import android.util.Log
import com.matin.happychat.ChatMessage
import com.matin.happychat.ChatServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class GRPCClient @Inject constructor() {

    private val channel: ManagedChannel = ManagedChannelBuilder.forAddress("10.0.2.2", 9090)
        .usePlaintext()
        .build()

    private val stub: ChatServiceGrpc.ChatServiceStub = ChatServiceGrpc.newStub(channel)

    fun createChatSession(): ChatSession {
        val messageFlow = MutableSharedFlow<ChatMessage>(replay = 1)

        val serviceStub = stub.chatStream(object : StreamObserver<ChatMessage> {
            override fun onNext(value: ChatMessage) {
                messageFlow.tryEmit(value)
            }

            override fun onError(t: Throwable) {
                Log.e("Chat", "gRPC Stream Error: ${t.message}", t)
            }

            override fun onCompleted() {
                Log.d("Chat", "gRPC Stream Completed")
            }
        })

        return ChatSession(serviceStub, messageFlow)
    }
}

class ChatSession(
    private val serviceStub: StreamObserver<ChatMessage>,
    private val _messageFlow: MutableSharedFlow<ChatMessage>
) {
    val messageFlow: SharedFlow<ChatMessage> = _messageFlow

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        try {
            serviceStub.onNext(
                ChatMessage.newBuilder().apply {
                    setSenderId(senderId)
                    setReceiverId(receiverId)
                    setMessage(message)
                }.build()
            )
        } catch (e: Exception) {
            Log.e("Chat", "Failed to send message: ${e.message}", e)
        }
    }

    fun close() {
        try {
            serviceStub.onCompleted()
        } catch (e: Exception) {
            Log.e("Chat", "Error closing chat session: ${e.message}", e)
        }
    }
}
