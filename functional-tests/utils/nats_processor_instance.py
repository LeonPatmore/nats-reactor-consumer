import logging

import docker
from docker.errors import NotFound


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
