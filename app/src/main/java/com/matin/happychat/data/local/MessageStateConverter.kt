package com.matin.happychat.data.local

import androidx.room.TypeConverter
import com.matin.happychat.common.model.MessageState

class MessageStateConverter {

    @TypeConverter
    fun fromMessageState(state: MessageState): String {
        return state.name
    }

    @TypeConverter
    fun toMessageState(stateName: String): MessageState {
        return MessageState.valueOf(stateName)
    }
}