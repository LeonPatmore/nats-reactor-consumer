package leon.patmore.nats

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import leon.patmore.nats.polling.NatsPoller
import leon.patmore.nats.processor.NatsProcessor
import org.springframework.stereotype.Service
import reactor.core.Disposable

@Service
class NatsSubscription(
    private val natsPoller: NatsPoller,
    private val natsProcessor: NatsProcessor
) {

    private val logger = KotlinLogging.logger {}

    private var pollerDisposable: Disposable? = null
    private var processorDisposable: Disposable? = null
    private var running = false

    @PostConstruct
    fun subscribe() {
        running = true
        processorDisposable = natsProcessor.process().subscribe()
        pollerDisposable = natsPoller.poll().subscribe()
    }

    @PreDestroy
    fun unsubscribe() {
        logger.info { "Closing nats subscription" }
        running = false
        pollerDisposable?.dispose()
        processorDisposable?.dispose()
    }

}
