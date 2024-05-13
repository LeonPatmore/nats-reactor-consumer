import requests


class ResourceNotFound(Exception):

    def __init__(self, domain: str, _id: str):
        self.domain = domain
        self._id = _id


class JsonServer:

    def __init__(self, host: str, port: int):
        self.host = host
        self.port = port

    def get_resource(self, domain: str, _id: str) -> dict:
        response = requests.get(f"http://{self.host}:{self.port}/{domain}/{_id}")
        if response.status_code == requests.codes["not_found"]:
            raise ResourceNotFound(domain, _id)
        return response.json()
