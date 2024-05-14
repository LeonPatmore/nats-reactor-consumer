import asyncio
import logging
from datetime import timedelta, datetime


async def wait_for(func: callable, max_time: timedelta = timedelta(seconds=30), delay: timedelta = timedelta(seconds=2)):
    start_time = datetime.now()
    previous_error = None
    while datetime.now() - start_time < max_time:
        try:
            func()
            return None
        except Exception as e:
            logging.info("Failed to wait due to " + str(e))
            previous_error = e
            await asyncio.sleep(delay.total_seconds())
    raise previous_error
