import requests
from pcm.Simulation import *

class HeadlessPCM:
    def __init__(self, url):
        self.url = url

    def isReachable(self):
        try:
            response = requests.get(self.url + "/rest/ping")
            return response.status_code == 200
        except:
            return False

    def clearSimulations(self):
        requests.get(self.url + "/rest/clear").text

    def prepareSimulation(self):
        return Simulation(self.url, requests.get(self.url + "/rest/prepare").text)