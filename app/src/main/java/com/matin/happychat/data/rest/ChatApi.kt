package com.matin.happychat.data.rest

import com.matin.happychat.data.model.MessageNetworkResponse
import com.matin.happychat.data.model.MessageRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {

    @POST("webhooks/rest/webhook")
    suspend fun sendMessage(@Body message: MessageRequest): List<MessageNetworkResponse>
}