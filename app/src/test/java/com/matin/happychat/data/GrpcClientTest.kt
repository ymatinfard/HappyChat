package com.matin.happychat.data

import com.matin.happychat.ChatMessage
import com.matin.happychat.data.remote.rest.grpc.ChatSession
import io.grpc.stub.StreamObserver
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class GrpcClientTest {

    private lateinit var streamObserver: StreamObserver<ChatMessage>
    private lateinit var chatSession: ChatSession

    @Before
    fun setup() {
        streamObserver = mockk(relaxed = true)
        chatSession = ChatSession(streamObserver)
    }

    @Test
    fun `sendMessage should send a message to the observer`() = runTest {
        val username = "TestUser"
        val message = "Hello gRPC!"

        val chatMessage = ChatMessage.newBuilder()
            .setSender(username)
            .setMessage(message)
            .build()

        chatSession.sendMessage(username, message)

        verify { streamObserver.onNext(chatMessage) }
    }

    @Test
    fun `closeStream should complete the stream`() = runTest {
        chatSession.closeStream()

        verify { streamObserver.onCompleted() }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
