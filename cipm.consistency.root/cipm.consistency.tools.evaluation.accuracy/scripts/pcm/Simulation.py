import requests
import json

class Simulation:
    def __init__(self, url, id):
        self.url = url
        self.id = id

    def startSimulation(self):
        requests.get(self.url + "/rest/" + self.id + "/start")

    def setConfiguration(self, config):
        requests.post(self.url + "/rest/" + self.id + "/set/config", data={
            "configJson": json.dumps(config)
        })

    def isFinished(self):
        return requests.get(self.url + "/rest/" + self.id + "/state").text in ("finished", "executed")

    def getResults(self):
        return requests.get(self.url + "/rest/" + self.id + "/results").content

    def setModelSet(self, mset):
        self.setRepository(mset["repository"])
        self.setSystem(mset["system"])
        self.setResourceEnvironment(mset["resenv"])
        self.setAllocation(mset["allocation"])
        self.setUsageModel(mset["usage"])

    def setRepository(self, file):
        self.setFile("repository", file)

    def setSystem(self, file):
        self.setFile("system", file)

    def setUsageModel(self, file):
        self.setFile("usagemodel", file)

    def setAllocation(self, file):
        self.setFile("allocation", file)

    def setResourceEnvironment(self, file):
        self.setFile("resourceenv", file)

    def setMonitorRepository(self, file):
        self.setFile("monitor", file)

    def setAdditional(self, file):
        self.setFile("addit", file)

    def setFile(self, type, model):
        requests.post(self.url + "/rest/" + self.id + "/set/" + type, files=dict(file=open(model, 'rb')))