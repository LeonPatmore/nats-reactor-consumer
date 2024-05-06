package leon.patmore.nats

import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.Nats
import io.nats.client.PullSubscribeOptions
import io.nats.client.api.ConsumerConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import java.time.Duration

@Configuration
class NetsConfiguration {

    @Bean
    fun natsSink(): NatsSink = Sinks.many().unicast().onBackpressureBuffer()

    @Bean
    fun natsConnection(): Connection = Nats.connect()

    @Bean
    fun pullSubscribeOptions(): PullSubscribeOptions = PullSubscribeOptions.Builder()
            .durable("fetch-durable-not-required")
            .configuration(ConsumerConfiguration.Builder().ackWait(Duration.ofSeconds(5)).build())
            .build()

    @Bean
    fun natsJetStreamSubscription(natsConnection: Connection,
                                  pullSubscribeOptions: PullSubscribeOptions): JetStreamSubscription =
        natsConnection.jetStream().subscribe("nats.test", pullSubscribeOptions)

}
