import com.matin.happychat.data.MessageRepositoryImpl
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.data.model.MessageEntity
import com.matin.happychat.data.model.MessageResponseNetwork
import com.matin.happychat.data.remote.rest.ChatApi
import com.matin.happychat.domain.MessageFactory.createMessage
import com.matin.happychat.domain.toEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MessageRepositoryImplTest {

    private val messageDao: MessageDao = mockk(relaxed = true)
    private val chatApi: ChatApi = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: MessageRepositoryImpl

    @Before
    fun setup() {
        repository = MessageRepositoryImpl(
            messageDao = messageDao,
            chatApi = chatApi,
            ioDispatcher = testDispatcher,
        )
    }

    @Test
    fun `insertToDb saves message and sends to server`() = runTest(testDispatcher) {
        val text = "Hello world"
        val messageSlot = slot<MessageEntity>()

        every { messageDao.insertMessageToDb(capture(messageSlot)) } just Runs

        repository.insertToDb(text)

        assertEquals(text, messageSlot.captured.content)
        coVerify(exactly = 1) { messageDao.insertMessageToDb(any()) }
        coVerify(exactly = 1) { chatApi.sendMessage(any()) }
    }

    @Test
    fun `retryFailedMessages retries messages from DB`() = runTest(testDispatcher) {
        val failedMessage = createMessage("Retry me")
        val response = mockk<MessageResponseNetwork>()
        coEvery { messageDao.getFailedMessages() } returns listOf(failedMessage.toEntity())
        coEvery { chatApi.sendMessage(any()) } returns mockk {
            every { response } returns mockk()
        }

        repository.retryFailedMessages()

        coVerify { chatApi.sendMessage(match { it.message == failedMessage.content }) }
    }
}
