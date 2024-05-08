package leon.patmore.nats.processor

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.Message
import leon.patmore.nats.NatsSink
import leon.patmore.nats.ack.NatsAcker
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class NatsProcessor(private val natsSink: NatsSink,
                    private val messageProcessor: NatsMessageProcessor,
                    private val natsAcker: NatsAcker) {

    private val logger = KotlinLogging.logger {}

    fun process() : Flux<Void> {
        return natsSink.asFlux()
            .doOnNext { logger.info { "Processing message" } }
            .flatMap { msg -> messageProcessor.process(msg)
                .flatMap { natsAcker.ack(msg) }
                .switchIfEmpty(natsAcker.ack(msg))
            }
            .doOnError {
                logger.warn { "Failed to process message!" }
            }
    }

}

fun interface NatsMessageProcessor {
    fun process(msg: Message) : Mono<Void>
}
