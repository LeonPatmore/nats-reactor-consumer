package leon.patmore.nats

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import leon.patmore.nats.polling.NatsPoller
import leon.patmore.nats.processor.NatsProcessor
import reactor.core.Disposable

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
        if (running) return
        running = true
        processorDisposable = natsProcessor.process().subscribe()
        pollerDisposable = natsPoller.poll().subscribe()
        logger.info { "Subscribe finished!" }
    }

    @PreDestroy
    fun unsubscribe() {
        logger.info { "Closing nats subscription" }
        running = false
        pollerDisposable?.dispose()
        processorDisposable?.dispose()
    }

}
