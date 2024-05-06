package leon.patmore.nats

import io.nats.client.Message
import reactor.core.publisher.Sinks

typealias NatsSink = Sinks.Many<Message>
