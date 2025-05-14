package com.matin.happychat.data.model

import com.google.gson.annotations.SerializedName

//data class MessageNetwork(
//    val id: String = ChatIdGenerator.nextId(),
//    val content: String,
//    val author: String = CURRENT_USER_ID,
//    val createdAt: Long = Instant.now().toEpochMilli(),
//)

data class MessageNetworkResponse(
    @SerializedName("recipient_id")
    val id: String,
    val text: String
)

data class MessageRequest(
    val sender: String,
    val message: String
)