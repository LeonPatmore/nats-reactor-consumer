package leon.patmore.nats

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.nats.client.Connection
import leon.patmore.nats.processor.NatsMessageProcessor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.kotlin.core.publisher.toMono

@SpringBootTest(classes = [NatsTestConfiguration::class])
@Testcontainers
class NatsConsumerE2ETest {

    @Autowired
    lateinit var natsConnection: Connection

    @Autowired
    lateinit var mockProcessor: NatsMessageProcessor

    @Container
    var natsContainer: GenericContainer<*> = GenericContainer("nats:2.9.25")
        .withExposedPorts(4222, 8222)

    @BeforeEach
    fun setUp() {
        every { mockProcessor.process(any()) } returns "".toMono().then()
    }

    @Test
    fun `test nats basic message`() {
        natsConnection.publish("nats.test", "hi".encodeToByteArray())

        verify(timeout = 10000) { mockProcessor.process(any()) }
        Thread.sleep(5000)
    }

}

@TestConfiguration
class NatsTestConfiguration {

    @Bean
    fun mockProcessor(): NatsMessageProcessor = mockk<NatsMessageProcessor>()

}
