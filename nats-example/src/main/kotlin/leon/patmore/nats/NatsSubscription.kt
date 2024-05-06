package leon.patmore.nats

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import leon.patmore.nats.polling.NatsPoller
import org.springframework.stereotype.Service
import reactor.core.Disposable

@Service
class NatsSubscription(
    private val natsPoller: NatsPoller,
) {

    private val logger = KotlinLogging.logger {}

    private var pollerDisposable: Disposable? = null
    private var running = false

    @PostConstruct
    fun subscribe() {
        running = true
        pollerDisposable = natsPoller.poll { running }.subscribe()
    }

    @PreDestroy
    fun unsubscribe() {
        logger.info { "Closing nats subscription" }
        running = false
        pollerDisposable?.dispose()
    }

}
