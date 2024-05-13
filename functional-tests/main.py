import json
import logging
import uuid

import nats
import pytest
import pytest_asyncio
from nats.js.api import StreamConfig, StorageType

from json_server_helpers import JsonServer
from utils import wait_for


@pytest_asyncio.fixture(scope="session")
async def nats_client():
    return await nats.connect()


@pytest_asyncio.fixture(scope="session")
async def setup_nats_stream(nats_client):
    stream_info = await nats_client.jetstream().add_stream(StreamConfig(name="test-stream",
                                                                        subjects=["nats.test"],
                                                                        storage=StorageType.MEMORY))

    logging.info("Stream info is " + json.dumps(stream_info.as_dict()))


@pytest.fixture(scope="session")
def json_server() -> JsonServer:
    return JsonServer(host="localhost", port=3010)


@pytest.mark.asyncio
async def test_simple(nats_client: nats.NATS, setup_nats_stream, json_server: JsonServer):
    my_id = str(uuid.uuid4())
    logging.info(f"Generating a new ID {my_id}")
    pub_ack = await nats_client.jetstream().publish("nats.test",
                                                    headers={"id": my_id},
                                                    payload="cool".encode())
    logging.info("Pub ack is " + json.dumps(pub_ack.as_dict()))

    wait_for(lambda: json_server.get_resource("messages", my_id))


@pytest.mark.asyncio
async def test_error(nats_client: nats.NATS, setup_nats_stream):
    my_id = str(uuid.uuid4())
    logging.info(f"Generating a new ID {my_id}")
    pub_ack = await nats_client.jetstream().publish("nats.test", headers={"id": my_id,
                                                                          "error": "true"}, payload="cool".encode())
    logging.info("Pub ack is " + json.dumps(pub_ack.as_dict()))

