import json
import logging

import nats
import pytest
import pytest_asyncio
from nats.js.api import StreamConfig, StorageType


@pytest_asyncio.fixture
async def nats_client():
    return await nats.connect()


@pytest_asyncio.fixture
async def setup_nats_stream(nats_client):
    stream_info = await nats_client.jetstream().add_stream(StreamConfig(name="test-stream",
                                                                        subjects=["nats.test"],
                                                                        storage=StorageType.MEMORY))

    logging.info("Stream info is " + json.dumps(stream_info.as_dict()))


@pytest.mark.asyncio
async def test_simple(nats_client: nats.NATS, setup_nats_stream):
    pub_ack = await nats_client.jetstream().publish("nats.test")
    logging.info("Pub ack is " + json.dumps(pub_ack.as_dict()))
