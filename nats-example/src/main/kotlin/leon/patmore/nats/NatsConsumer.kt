package leon.patmore.nats

import io.nats.client.Nats
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class NatsConsumer() {

    init {
        val connection = Nats.connect()

        val sub = connection.subscribe("nats.test")

        println("Connection made!")

        while(true) {
            val message = sub.nextMessage(Duration.ofDays(1))

            println("Received ${message.subject}")

//            message.ack()
        }
    }

}
