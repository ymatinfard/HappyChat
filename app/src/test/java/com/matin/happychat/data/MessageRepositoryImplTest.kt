import com.matin.happychat.data.MessageRepositoryImpl
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.data.model.MessageEntity
import com.matin.happychat.data.rest.MessageApi
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
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MessageRepositoryImplTest {

    private val messageDao: MessageDao = mockk(relaxed = true)
    private val messageApi: MessageApi = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var repository: MessageRepositoryImpl

    @Before
    fun setup() {
        repository = MessageRepositoryImpl(
            messageDao = messageDao,
            messageApi = messageApi,
            ioDispatcher = testDispatcher,
            externalSupervisorScope = testScope
        )
    }

    @Test
    fun `insertToDb saves message and sends to server`() = runTest(testDispatcher) {
        val text = "Hello world"
        val messageSlot = slot<MessageEntity>()

        coEvery { messageApi.sendMessage(any()) } returns mockk {
            every { toEntity() } returns mockk()
        }
        every { messageDao.insertMessageToDb(capture(messageSlot)) } just Runs

        repository.insertToDb(text)

        assertEquals(text, messageSlot.captured.content)
        coVerify(exactly = 1) { messageDao.insertMessageToDb(any()) }
        coVerify(exactly = 1) { messageApi.sendMessage(any()) }
    }

    @Test
    fun `retryFailedMessages retries messages from DB`() = runTest(testDispatcher) {
        val failedMessage = createMessage("Retry me")
        coEvery { messageDao.getFailedMessages() } returns listOf(failedMessage.toEntity())
        coEvery { messageApi.sendMessage(any()) } returns mockk {
            every { toEntity() } returns mockk()
        }

        repository.retryFailedMessages()

        coVerify { messageApi.sendMessage(match { it.id == failedMessage.id }) }
    }
}
