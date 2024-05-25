package leon.patmore.nats.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClient() = WebClient.create("http://localhost:3010/messages")

}
