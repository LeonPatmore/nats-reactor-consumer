package leon.patmore.nats.test

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(TestProcessorProperties::class)
class WebClientConfiguration {

    @Bean
    fun webClient(properties: TestProcessorProperties) =
        WebClient.create("http://${properties.jsonServerHost}:${properties.jsonServerPort}/messages")

}
