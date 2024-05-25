package leon.patmore.nats.processor

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.Message
import leon.patmore.nats.NatsSink
import leon.patmore.nats.ack.NatsAcker
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Service
class NatsProcessor(private val natsSink: NatsSink,
                    private val messageProcessor: NatsMessageProcessor,
                    private val natsAcker: NatsAcker,
                    private val nackDelayProvider: NackDelayProvider) {

    private val logger = KotlinLogging.logger {}

    fun process() : Flux<Void> {
        return natsSink.asFlux()
            .publishOn(Schedulers.parallel())
            .doOnNext { logger.info { "Processing message ${it.metaData()}" } }
            .flatMap { msg -> messageProcessor.process(msg)
                .flatMap { natsAcker.ack(msg) }
                .switchIfEmpty(natsAcker.ack(msg))
                .onErrorResume { err ->
                    logger.warn(err) { "Failed to process message, nacking!" }
                    msg.nakWithDelay(nackDelayProvider.get(msg))
                    "".toMono().then()
                }
            }
    }

}

fun interface NatsMessageProcessor {
    fun process(msg: Message) : Mono<Void>
}
