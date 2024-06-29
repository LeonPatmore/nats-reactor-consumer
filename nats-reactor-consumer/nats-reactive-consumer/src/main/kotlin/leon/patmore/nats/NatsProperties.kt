package leon.patmore.nats

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("nats.consumer")
data class NatsProperties(@NotEmpty val host: String = "localhost",
                          @Positive val port: Int = 4222)
