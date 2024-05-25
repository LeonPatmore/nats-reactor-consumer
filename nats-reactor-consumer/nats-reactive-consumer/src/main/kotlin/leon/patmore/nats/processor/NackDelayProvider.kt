package leon.patmore.nats.processor

import io.nats.client.Message
import java.time.Duration

fun interface NackDelayProvider {

    fun get(msg: Message) : Duration

}
