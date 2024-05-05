import nats
import pytest


@pytest.mark.asyncio
async def test_simple():
    nc = await nats.connect()

    await nc.publish("nats.test")
