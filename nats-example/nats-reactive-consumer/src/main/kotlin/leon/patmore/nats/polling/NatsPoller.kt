package leon.patmore.nats.polling

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.JetStreamSubscription
import leon.patmore.nats.NatsSink
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration


@Service
class NatsPoller(private val sink: NatsSink,
                 private val jetStreamSubscription: JetStreamSubscription) {

    private val logger = KotlinLogging.logger {}

    fun poll(): Flux<Void> {
        return Mono.fromCallable {
            return@fromCallable jetStreamSubscription.fetch(10, Duration.ofMillis(500))
        }.flatMapMany { it.toFlux() }.doOnNext {
            logger.info { "Emitting message" }
            sink.tryEmitNext(it)
        }.doOnError {
            logger.warn(it) { "Failed to emit message" }
        }
            .then()
            .repeat()
    }

}
