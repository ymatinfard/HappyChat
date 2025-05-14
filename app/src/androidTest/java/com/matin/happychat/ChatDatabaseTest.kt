package com.matin.happychat

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.matin.happychat.common.model.MessageState
import com.matin.happychat.data.local.ChatDatabase
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.domain.Message
import com.matin.happychat.domain.toEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ChatDatabaseTest {

    lateinit var messageDao: MessageDao
    lateinit var db: ChatDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db =
            Room.inMemoryDatabaseBuilder(context, ChatDatabase::class.java).allowMainThreadQueries()
                .build()
        messageDao = db.messageDao()
    }

    @Test
    fun insertMessage_should_insert_a_message_into_the_database() = runTest {
        val entity = Message(
            id = 1,
            content = "Hello",
            author = "Me"
        ).toEntity()

        messageDao.insertMessageToDb(entity)
        val result = messageDao.getAllMessages().first()

        assert(result.contains(entity))
    }

    @Test
    fun getAllMessages_should_return_all_messages_from_the_database() = runTest {
        val entity1 = Message(
            id = 1,
            content = "Hello",
            author = "Me"
        ).toEntity()
        val entity2 = Message(
            id = 2,
            content = "Hi",
            author = "You"
        ).toEntity()

        messageDao.insertMessageToDb(entity1)
        messageDao.insertMessageToDb(entity2)

        val result = messageDao.getAllMessages().first()

        assert(result.contains(entity1))
        assert(result.contains(entity2))
    }

    @Test
    fun deleteMessage_should_delete_a_message_from_the_database() = runTest {
        val entity = Message(
            id = 1,
            content = "Hello",
            author = "Me"
        ).toEntity()

        messageDao.insertMessageToDb(entity)
        messageDao.deleteAllMessages()

        val result = messageDao.getAllMessages().first()

        assert(result.isEmpty())
    }

    @Test
    fun updateMessageState_should_update_a_message_state_in_the_database() = runTest {
        val entity = Message(
            id = 1,
            content = "Hello",
            author = "Me",
            state = MessageState.PENDING
        ).toEntity()

        messageDao.insertMessageToDb(entity)
        messageDao.updateMessageState(entity.id, MessageState.SENT)
        val result = messageDao.getAllMessages().first()

        assert(result.first { it.id == entity.id }.state == MessageState.SENT)
    }

    @After
    fun teardown() {
        db.close()
    }
}