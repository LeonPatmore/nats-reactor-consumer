package leon.patmore.nats.test

import io.github.oshai.kotlinlogging.KotlinLogging
import io.nats.client.Message
import leon.patmore.nats.processor.NatsMessageProcessor
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import kotlin.math.min

@Service
class MessageProcessor(private val webClient: WebClient) : NatsMessageProcessor {

    private val logger = KotlinLogging.logger {}

    override fun process(msg: Message): Mono<Void> {
        logger.info { msg.metaData() }
        val id = msg.headers?.get("id")?.first()!!
        val delay = msg.getDelay()
        val shouldError = msg.headers?.get("error")?.firstOrNull()?.let { it.equals("true", ignoreCase = true) } ?: false
        logger.info { "Processing ID $id with delay $delay and error $shouldError" }
        return Mono.delay(Duration.ofSeconds(delay))
            .flatMap {
                if (shouldError) {
                    Mono.error(RuntimeException("asd"))
                } else {
                    it.toMono()
                }
            }
            .flatMap {
                logger.info { "Sending ID $id to JSON server" }
                webClient.post()
                    .bodyValue(Message(id))
                    .retrieve()
                    .toBodilessEntity()
                    .then()
            }
            .onErrorResume(WebClientResponseException::class.java) {
                return@onErrorResume "".toMono().then()
            }
    }

    private fun Message.getDelay() : Long {
        val attempt = this.metaData().deliveredCount()
        return this.headers?.get("delays")
            ?.firstOrNull()
            ?.toString()
            ?.ifBlank { null }
            ?.split(",")
            ?.map { it.toLong() }
            ?.let { it.elementAtOrNull(min(attempt.toInt() - 1, it.size - 1)) } ?: 0L
    }

}

data class Message(val id: String)
