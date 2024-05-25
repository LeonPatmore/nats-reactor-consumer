package leon.patmore.nats.processor

import io.nats.client.Message
import java.time.Duration
import kotlin.math.min

class NackDelayFromBackoffs(private val backoffs: List<Duration>) : NackDelayProvider {
    override fun get(msg: Message) = backoffs[min(backoffs.size - 1, msg.getAttemptNumber())]
    private fun Message.getAttemptNumber() : Int = this.metaData()?.deliveredCount()?.toInt()?.dec() ?: 0
}
