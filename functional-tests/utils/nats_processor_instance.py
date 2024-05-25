import logging
import os
import threading

from utils.commands import run
from utils.config import settings

JAVA_HOME = settings.JAVA_HOME


class ServiceInstance(object):

    def start(self):
        raise NotImplementedError()

    def stop(self):
        raise NotImplementedError()


class LocalProcessInstance(ServiceInstance):

    class _Thread(threading.Thread):

        def __init__(self, cmd: str):
            super().__init__()
            self.process = None
            self.cmd = cmd

        def run(self):
            self.process = run(self.cmd)

        def stop(self):
            os.system(f"taskkill /pid {self.process.pid} /f /t")

    def __init__(self):
        self.thread = None
        self.cmd = self._get_cmd()

    @staticmethod
    def _get_cmd() -> str:
        return f"{os.path.join(os.pardir, 'nats-example', 'gradlew')} "\
               f"-p {os.path.join(os.pardir, 'nats-example')} "\
               f"-Dorg.gradle.java.home={JAVA_HOME} "\
               f"nats-test-processor:bootRun"

    def start(self):
        logging.info("Starting processor")
        self.thread = self._Thread(self.cmd)
        self.thread.start()

    def stop(self):
        logging.info("Stopping processor")
        self.thread.stop()
        self.thread.join()
