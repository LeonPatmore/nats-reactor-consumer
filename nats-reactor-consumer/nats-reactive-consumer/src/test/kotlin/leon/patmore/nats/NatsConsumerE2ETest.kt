package leon.patmore.nats

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.nats.client.Connection
import kotlinx.coroutines.runBlocking
import leon.patmore.nats.processor.NatsMessageProcessor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.kotlin.core.publisher.toMono
import kotlin.time.Duration.Companion.seconds


@SpringBootTest(classes = [NatsTestConfiguration::class])
@Testcontainers
internal class NatsConsumerE2ETest : NatsTestContainer() {

    @Autowired
    lateinit var natsConnection: Connection

    @Autowired
    lateinit var mockProcessor: NatsMessageProcessor

    @BeforeEach
    fun setUp() {
        every { mockProcessor.process(any()) } returns "".toMono().then()
    }

    @Test
    fun `test nats basic message`() {
        natsConnection.publish("nats.test", "hi".encodeToByteArray())

        verify(timeout = 5000) { mockProcessor.process(any()) }
        runBlocking {
            eventually(5.seconds) {
                natsConnection.jetStreamManagement()
                    .getConsumerInfo("test-stream", "consumer-name")
                    .numAckPending shouldBe 0
            }
        }
    }

}

@TestConfiguration
internal class NatsTestConfiguration {

    @Bean
    fun mockProcessor(): NatsMessageProcessor = mockk<NatsMessageProcessor>()

}
