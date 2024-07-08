import logging
import os
import threading

import docker
from docker.errors import NotFound

from utils.commands import run
from utils.config import settings

JAVA_HOME = settings.JAVA_HOME


class ServiceInstance(object):

    def start(self, log_file_postfix: str = ""):
        raise NotImplementedError()

    def stop(self):
        raise NotImplementedError()


class DockerProcessInstance(ServiceInstance):

    def __init__(self):
        self.log_file_postfix = None
        self.container = None

    def start(self, log_file_postfix: str = ""):
        self.log_file_postfix = log_file_postfix
        client = docker.from_env()
        self.container = client.containers.run("nats-test-consumer:latest",
                                               environment={
                                                   "NATS_CONSUMER_HOST": "nats",
                                                   "NATS_TEST_PROCESSOR_JSON_SERVER_HOST": "jsonserver",
                                                   "NATS_TEST_PROCESSOR_JSON_SERVER_PORT": "3000"
                                               },
                                               detach=True,
                                               remove=True,
                                               network="local_nats_net")
        logging.info(f"Container {self.container.name} has started")

    def write_log_file(self):
        logs = self.container.logs()
        f = open(f"stdout{self.log_file_postfix}.txt", "wb")
        f.write(logs)
        f.close()

    def stop(self):
        try:
            self.write_log_file()
        except NotFound:
            return
        self.container.stop()


class LocalProcessInstance(ServiceInstance):

    class _Thread(threading.Thread):

        def __init__(self, cmd: str, log_file_postfix: str):
            super().__init__()
            self.process = None
            self.cmd = cmd
            self.log_file_postfix = log_file_postfix

        def run(self):
            self.process = run(self.cmd, self.log_file_postfix)

        def stop(self):
            os.system(f"taskkill /pid {self.process.pid} /f /t")

    def __init__(self):
        self.thread = None
        self.cmd = self._get_cmd()

    @staticmethod
    def _get_cmd() -> str:
        return f"{os.path.join(os.pardir, 'nats-reactor-consumer', 'gradlew')} "\
               f"-p {os.path.join(os.pardir, 'nats-reactor-consumer')} "\
               f"-Dorg.gradle.java.home={JAVA_HOME} "\
               f"nats-test-consumer:bootRun"

    def start(self, log_file_postfix: str = ""):
        logging.info("Starting processor")
        self.thread = self._Thread(self.cmd, log_file_postfix)
        self.thread.start()

    def stop(self):
        logging.info("Stopping processor")
        self.thread.stop()
        self.thread.join()
