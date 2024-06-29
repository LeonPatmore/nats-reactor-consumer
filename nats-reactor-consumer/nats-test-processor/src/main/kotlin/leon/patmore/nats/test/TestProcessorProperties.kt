package leon.patmore.nats.test

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("nats.test.processor")
data class TestProcessorProperties(@NotEmpty val jsonServerHost: String = "localhost",
                                   @Positive val jsonServerPort: Int = 3010)
