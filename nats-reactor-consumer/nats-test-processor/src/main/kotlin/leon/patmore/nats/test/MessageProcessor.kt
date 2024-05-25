package leon.patmore.nats.test

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.Message
import leon.patmore.nats.processor.NatsMessageProcessor
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class MessageProcessor(private val webClient: WebClient) : NatsMessageProcessor {

    private val logger = KotlinLogging.logger {}

    override fun process(msg: Message): Mono<Void> {
        return Mono.fromCallable {
            return@fromCallable msg.headers?.get("id")?.first()
        }
            .flatMap {
                val shouldError = msg.headers?.get("error")?.isNotEmpty() ?: false
                if (shouldError) {
                    Mono.error(RuntimeException("asd"))
                } else {
                    it.toMono()
                }
            }
            .flatMap {
                logger.info { "Sending ID $it" }
                webClient.post()
                    .bodyValue(Message(it!!))
                    .retrieve()
                    .toBodilessEntity()
                    .then()
            }.onErrorResume(WebClientResponseException::class.java) {
                return@onErrorResume "".toMono().then()
            }
    }

}

data class Message(val id: String)
