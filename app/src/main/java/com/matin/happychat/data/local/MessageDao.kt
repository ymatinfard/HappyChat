package com.matin.happychat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    fun insertMessageToDb(message: MessageEntity)

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET state = :newState WHERE id = :messageId")
    fun updateMessageState(messageId: String, newState: MessageState)

    @Query("DELETE FROM messages")
    fun deleteAllMessages()

    @Query("SELECT * FROM messages WHERE state = 'FAILED'")
    fun getFailedMessages(): List<MessageEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM messages WHERE state = 'PENDING')")
    fun hasPendingMessages(): Flow<Boolean>
}