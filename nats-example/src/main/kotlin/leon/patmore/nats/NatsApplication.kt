package leon.patmore.nats

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NatsApplication

fun main(args: Array<String>) {
	runApplication<NatsApplication>(*args)
}
