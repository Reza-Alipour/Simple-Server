import requests
from typing import Dict


class ClientData:
    json_headers = {'Content-Type': 'application/json'}

    def __init__(self, url):
        self.url = url
        self.session = requests.Session()
        self.role = ""

    def post(self, endpoint: str, data: Dict = None, header: Dict[str, str] = None, params: Dict = None,
             files: Dict = None):
        if header is None and files is None:
            header = ClientData.json_headers
        return self.session.post(self.url + endpoint, json=data, headers=header, params=params, files=files)

    def get(self, endpoint: str, params: Dict = None, header: Dict[str, str] = None):
        if header is None:
            header = ClientData.json_headers
        return self.session.get(self.url + endpoint, headers=header, params=params)
