package com.matin.happychat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.common.model.MessageType
import com.matin.happychat.data.local.MessageStateConverter

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val author: String,
    val timestamp: Long,
    val type: MessageType,
    @TypeConverters(MessageStateConverter::class)
    val state: MessageState,

    // fields for image
//    val imageUri: String? = null,
//    val width: Int? = null,
//    val height: Int? = null,
//
//    // fields for voice
//    val voicePath: String? = null,
//    val durationMs: Long? = null
)
