package leon.patmore.nats

import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.Nats
import io.nats.client.PullSubscribeOptions
import io.nats.client.api.ConsumerConfiguration
import leon.patmore.nats.polling.NatsPoller
import leon.patmore.nats.processor.NatsProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import java.time.Duration

@Configuration
class NetsConfiguration {

    @Bean
    fun natsSink(): NatsSink = Sinks.many().multicast().onBackpressureBuffer()

    @Bean
    fun natsConnection(): Connection = Nats.connect()

    @Bean
    fun pullSubscribeOptions(): PullSubscribeOptions = PullSubscribeOptions.Builder()
            .durable("consumer-name")
            .configuration(ConsumerConfiguration.Builder()
                .ackWait(Duration.ofSeconds(3))
                .maxDeliver(2L)
                .build())
            .build()

    @Bean
    fun natsJetStreamSubscription(natsConnection: Connection,
                                  pullSubscribeOptions: PullSubscribeOptions): JetStreamSubscription =
        natsConnection.jetStream().subscribe("nats.test", pullSubscribeOptions)

    @Bean
    fun natsSubscription(natsPoller: NatsPoller, natsProcessor: NatsProcessor) =
        NatsSubscription(natsPoller, natsProcessor)

}
