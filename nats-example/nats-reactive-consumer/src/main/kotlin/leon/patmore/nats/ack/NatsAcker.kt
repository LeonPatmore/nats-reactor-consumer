package leon.patmore.nats.ack

import io.nats.client.Message
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

fun interface NatsAcker {
    fun ack(message: Message) : Mono<Void>
}

@Service
class SyncNatsAcker : NatsAcker {
    override fun ack(message: Message): Mono<Void> {
        return Mono.fromCallable { message.ackSync(Duration.ofSeconds(1)) }.then()
    }
}
