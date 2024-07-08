import asyncio
import json
import logging
import uuid
from asyncio import sleep

import nats
import pytest
import pytest_asyncio
from dynaconf import settings
from nats.js import JetStreamContext
from nats.js.api import StreamConfig, StorageType

from utils.json_server_helpers import JsonServer
from utils.nats_consumer import NatsConsumer
from utils.nats_processor_instance import DockerProcessInstance
from utils.wait_for import wait_for


@pytest_asyncio.fixture
async def nats_client():
    client = await nats.connect(f"nats://{settings.get('NATS_HOST', 'localhost')}:4222")
    yield client, client.jetstream()
    await client.close()


@pytest_asyncio.fixture
async def setup_nats_stream(nats_client):
    _, js = nats_client
    stream_info = await js.add_stream(StreamConfig(name="test-stream",
                                                   subjects=["nats.test"],
                                                   storage=StorageType.MEMORY))

    logging.info("Stream info is " + json.dumps(stream_info.as_dict()))


@pytest_asyncio.fixture(scope="session")
async def start_processor():
    processor = DockerProcessInstance()
    processor.start()
    yield processor
    processor.stop()


@pytest_asyncio.fixture
async def setup_nats_stream_dlq(nats_client) -> NatsConsumer:
    _, js = nats_client

    stream_info = await js.add_stream(StreamConfig(name="test-stream-dlq",
                                                   subjects=[
                                                       "$JS.EVENT.ADVISORY.CONSUMER.MAX_DELIVERIES.test-stream.*"],
                                                   storage=StorageType.MEMORY))

    consumer = NatsConsumer(js, "$JS.EVENT.ADVISORY.CONSUMER.MAX_DELIVERIES.test-stream.*")
    task = asyncio.create_task(consumer.start())
    yield consumer
    logging.info("Cleaning up dlq consumer")
    task.cancel()


@pytest.fixture(scope="session")
def json_server() -> JsonServer:
    return JsonServer(host=settings.get("JSON_SERVER_HOST", "localhost"),
                      port=int(settings.get("JSON_SERVER_PORT", 3010)))


async def generate_message(js, delays: list = None, error: bool = False) -> tuple:
    delays_as_string = ",".join(str(x) for x in delays) if delays else ""
    my_id = str(uuid.uuid4())
    logging.info(f"Generating a new ID {my_id}")
    pub_ack = await js.publish("nats.test",
                               headers={"id": my_id, "delays": delays_as_string, "error": str(error).lower()},
                               payload="cool".encode())
    logging.info("Pub ack is " + json.dumps(pub_ack.as_dict()))
    return my_id, pub_ack


@pytest.mark.asyncio
async def test_setup_nats_stream(nats_client, setup_nats_stream, json_server: JsonServer):
    _, js = nats_client


@pytest.mark.asyncio
async def test_simple_message_is_processed(nats_client, setup_nats_stream, start_processor, json_server: JsonServer):
    _, js = nats_client
    my_id, _ = await generate_message(js)

    await wait_for(lambda: json_server.get_resource("messages", my_id))
    await wait_for(acks_pending_is_zero_func(js))


@pytest.mark.asyncio
async def test_message_that_errors_goes_to_dlq(nats_client,
                                               start_processor,
                                               setup_nats_stream,
                                               setup_nats_stream_dlq: NatsConsumer):
    _, js = nats_client
    _, pub_ack = await generate_message(js, error=True)

    await wait_for(lambda: next(x for x in setup_nats_stream_dlq.msgs
                                if json.loads(x.data)["stream_seq"] == pub_ack.seq))


@pytest.mark.asyncio
async def test_message_that_does_not_ack_in_time_goes_to_dlq(nats_client,
                                                             start_processor,
                                                             setup_nats_stream,
                                                             setup_nats_stream_dlq: NatsConsumer):
    _, js = nats_client
    _, pub_ack = await generate_message(js, delays=[30])

    await wait_for(lambda: next(x for x in setup_nats_stream_dlq.msgs
                                if json.loads(x.data)["stream_seq"] == pub_ack.seq))


@pytest.mark.asyncio
async def test_message_when_service_dies_it_is_retried(request, nats_client, setup_nats_stream, json_server: JsonServer):
    _, js = nats_client
    first_processor = DockerProcessInstance()
    request.addfinalizer(first_processor.stop)
    first_processor.start(log_file_postfix="first")
    await sleep(10) # TODO: We should wait until the service is running via a healthcheck.

    my_id, _ = await generate_message(js, delays=[10, 0])

    await sleep(3)
    first_processor.stop()
    await sleep(3)
    second_processor = DockerProcessInstance()
    request.addfinalizer(second_processor.stop)
    second_processor.start(log_file_postfix="second")

    await wait_for(lambda: json_server.get_resource("messages", my_id))
    await wait_for(acks_pending_is_zero_func(js))


def acks_pending_is_zero_func(js) -> callable:
    async def acks_pending_is_zero() -> bool():
        if await get_ack_pending_count(js) > 0:
            raise Exception("Max ack pending is greater than zero!")
    return acks_pending_is_zero


async def get_ack_pending_count(nats_client: JetStreamContext) -> int:
    consumer_config = await nats_client.consumer_info("test-stream", "consumer-name")
    ack_pending = consumer_config.num_ack_pending
    logging.info(f"Number ack pending is {ack_pending}")
    return consumer_config.num_ack_pending
