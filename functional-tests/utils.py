import logging
import time
from datetime import timedelta, datetime


def wait_for(func: callable, max_time: timedelta = timedelta(seconds=30), delay: timedelta = timedelta(seconds=5)):
    start_time = datetime.now()
    previous_error = None
    while datetime.now() - start_time < max_time:
        try:
            func()
        except Exception as e:
            logging.info("Failed to wait due to " + str(e))
            time.sleep(delay.total_seconds())
    return previous_error
