import logging

from nats.js import JetStreamContext


class NatsConsumer:

    def __init__(self, jet_stream: JetStreamContext, subject_name: str):
        self.jet_stream = jet_stream
        self.subject_name = subject_name
        self.msgs = []

    async def start(self):
        logging.info(f"Starting nats consumer for subject [ {self.subject_name} ]")
        sub = await self.jet_stream.subscribe(self.subject_name, durable="test-consumer")

        while True:
            msg = await sub.next_msg(timeout=None)
            logging.info(f"Received message is {str(msg.metadata)}")
            self.msgs.append(msg)
            await msg.ack()
