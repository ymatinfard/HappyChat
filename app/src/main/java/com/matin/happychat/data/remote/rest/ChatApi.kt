package com.matin.happychat.data.remote.rest

import com.matin.happychat.data.model.MessageResponseNetwork
import com.matin.happychat.data.model.MessageRequestNetwork
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {

    @POST("webhooks/rest/webhook")
    suspend fun sendMessage(@Body message: MessageRequestNetwork): List<MessageResponseNetwork>
}