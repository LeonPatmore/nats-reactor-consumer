package leon.patmore.nats

import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.Nats
import io.nats.client.PullSubscribeOptions
import io.nats.client.api.AckPolicy
import io.nats.client.api.ConsumerConfiguration
import leon.patmore.nats.polling.NatsPoller
import leon.patmore.nats.processor.NackDelayFromBackoffs
import leon.patmore.nats.processor.NatsProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    fun backOffPolicy() = listOf(Duration.ofSeconds(1),
        Duration.ofSeconds(2),
        Duration.ofSeconds(3))

    @Bean
    fun pullSubscribeOptions(backOffPolicy: List<Duration>): PullSubscribeOptions = PullSubscribeOptions.Builder()
            .durable("consumer-name")
            .configuration(ConsumerConfiguration.Builder()
                .ackPolicy(AckPolicy.Explicit) // This is important otherwise acking doesn't work, even though this is a default setting according to the docs.
                .ackWait(Duration.ofSeconds(3))
//                .backoff(*backOffPolicy.toTypedArray())
                .maxDeliver(backOffPolicy.size.toLong() + 1L)
                .build())
            .build()

    @Bean
    @ConditionalOnMissingBean
    fun backDelayProvider(backOffPolicy: List<Duration>) = NackDelayFromBackoffs(backOffPolicy)

    @Bean
    fun natsJetStreamSubscription(natsConnection: Connection,
                                  pullSubscribeOptions: PullSubscribeOptions): JetStreamSubscription =
        natsConnection.jetStream().subscribe("nats.test", pullSubscribeOptions)

    @Bean
    fun natsSubscription(natsPoller: NatsPoller, natsProcessor: NatsProcessor) =
        NatsSubscription(natsPoller, natsProcessor)

}
