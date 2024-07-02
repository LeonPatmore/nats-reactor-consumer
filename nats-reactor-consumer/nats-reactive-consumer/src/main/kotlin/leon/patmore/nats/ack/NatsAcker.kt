package leon.patmore.nats.ack

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import io.nats.client.Message
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

fun interface NatsAcker {
    fun ack(message: Message) : Mono<Void>
}

@Service
class SyncNatsAcker(private val meterRegistry: MeterRegistry) : NatsAcker {

    private val logger = KotlinLogging.logger {}

    override fun ack(message: Message): Mono<Void> {
        return Mono.fromCallable {
            logger.info { "Acking message ${message.metaData()}" }
            message.ackSync(Duration.ofSeconds(1))
        }
            .doOnSuccess { meterRegistry.counter("nats_acked").increment() }
            .then()
    }

}
