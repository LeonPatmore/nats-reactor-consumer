package leon.patmore.nats.processor

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

    fun process() : Flux<Void> {
        return natsSink.asFlux()
            .flatMap { messageProcessor.process(it).thenReturn(it) }
            .flatMap { natsAcker.ack(it) }
    }

}

fun interface NatsMessageProcessor {
    fun process(msg: Message) : Mono<Void>
}
