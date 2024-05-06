package leon.patmore.nats

import io.nats.client.JetStream
import io.nats.client.Nats
import io.nats.client.PullSubscribeOptions
import io.nats.client.api.ConsumerConfiguration
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class NatsConsumer() {

    init {
        val connection = Nats.connect()

        val js: JetStream = connection.jetStream()

        val sub = js.subscribe("nats.test", PullSubscribeOptions.Builder()
            .durable("fetch-durable-not-required")
            .configuration(ConsumerConfiguration.Builder().ackWait(Duration.ofSeconds(5)).build())
            .build())

        println("Connection made!")

        var running = true
        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutting down...")
            running = false
        })

        connection.flush(Duration.ofSeconds(5))

        while(running) {
            println("Pending messages" + sub.consumerInfo.numPending)
            println("Ack pending messages" + sub.consumerInfo.numAckPending)
            val messages = sub.fetch(10, Duration.ofMillis(500)) // Pull up to 10 messages with a timeout of 5 seconds
            for (message in messages) {
                println("Received ${message.subject}")
                message.ack()
            }
        }
    }

}
