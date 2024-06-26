package leon.patmore.nats

import io.nats.client.Nats
import io.nats.client.api.StorageType
import io.nats.client.api.StreamConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container

open class NatsTestContainer {

    companion object {

        @Container
        @JvmStatic
        var natsContainer: GenericContainer<*> = GenericContainer("nats:2.9.25")
            .withExposedPorts(4222, 8222)
            .withCommand("--name", "N1", "--js", "-m", "8222")

        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            val mappedPort = natsContainer.getMappedPort(4222)
            registry.add("nats.consumer.port") { mappedPort }
            Nats.connect("nats://localhost:${mappedPort}")
                .jetStreamManagement()
                .addStream(
                    StreamConfiguration.builder()
                    .name("test-stream")
                    .subjects("nats.test")
                    .storageType(StorageType.Memory)
                    .build())
        }

    }

}
