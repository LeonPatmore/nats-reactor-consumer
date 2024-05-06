package leon.patmore.nats.polling

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.JetStreamSubscription
import leon.patmore.nats.NatsSink
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.util.function.BooleanSupplier


@Service
class NatsPoller(private val sink: NatsSink,
                 private val jetStreamSubscription: JetStreamSubscription) {

    private val logger = KotlinLogging.logger {}

    fun poll(running: BooleanSupplier): Flux<Void> {
        return Mono.fromCallable {
            jetStreamSubscription.fetch(10, Duration.ofMillis(500))
                .also { if (it.size > 0) {
                    logger.info { "Found ${it.size} messages" }
                } }
                .toFlux()
                .doOnNext { sink.tryEmitNext(it) } }
            .then()
            .repeat(running)
    }

}
