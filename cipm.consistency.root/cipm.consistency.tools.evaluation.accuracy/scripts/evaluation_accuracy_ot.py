#!/usr/bin/env python
# coding: utf-8

# In[1]:


import concurrent

# imports
import requests
import json
from pcm.Simulation import *
from pcm.HeadlessPCM import *

from tqdm import tqdm
from os import listdir
import threading
import os
import statistics
from shutil import copyfile
import time
from scipy import stats
from scipy.stats import wasserstein_distance
import random
from concurrent.futures import *
from numpy import cumsum
import numpy as np

import matplotlib.pyplot as plt


# In[2]:


# pcm clients
pcm_clients = [HeadlessPCM("http://127.0.0.1:8080"), HeadlessPCM("http://127.0.0.1:8090"),
               HeadlessPCM("http://127.0.0.1:8100"), HeadlessPCM("http://127.0.0.1:8110")]
pcm_process_pool = ThreadPoolExecutor(max_workers=len(pcm_clients))
unoccupied_clients = list(range(0, len(pcm_clients), 1))

# data
data_base_path = "../test-data/opstime-monitoring-and-models/experiment-executions/"
data_prefix = "execution"
data_nums = range(1, 11, 1)

data_monitoring_path = "monitoring/reduced.json"
data_model_path = "models"

observed_service = "_xliLoDVXEeqPG_FgW3bi6Q"
observed_service_name = "confirmOrder"

monitoring_files = []
for k in data_nums:
    with open(data_base_path + data_prefix + str(k) + "/" + data_monitoring_path, "r") as infile:
        monitoring_files.append(json.load(infile))

# In[ ]:


cut_size_front = 7
cut_size_end = 1


# simulation
simulation_config = {
    "experimentName": "Paper Evaluation Simulation",
    "simulationTime": 800000,
    "maximumMeasurementCount": 150000,
    "useFixedSeed": False,
    "parallelizeRepetitions": False,
    "repetitions": 1,
    "type": "SimuCom",
    "simuComStoragePath": None
}


# In[5]:

temp_base_path = "temp/"

temp_files = {
    "repository": "repository.repository",
    "system": "system.system",
    "resenv": "resourceenv.resourceenvironment",
    "allocation": "allocation.allocation",
    "usage": "usage.usagemodel"
}


# In[6]:


def generateTempFileSet(counter):
    return {
        "repository": "repository" + str(counter) + ".repository",
        "system": "system" + str(counter) + ".system",
        "resenv": "resourceenv" + str(counter) + ".resourceenvironment",
        "allocation": "allocation" + str(counter) + ".allocation",
        "usage": "usage" + str(counter) + ".usagemodel"
    }


def generateFullTempFileSet(file_set):
    return {
        "repository": temp_base_path + file_set["repository"],
        "system": temp_base_path + file_set["system"],
        "resenv": temp_base_path + file_set["resenv"],
        "allocation": temp_base_path + file_set["allocation"],
        "usage": temp_base_path + file_set["usage"]
    }


def transformClosureTemporary(model_set, model_num_else, counter):
    replaceStrings = []

    temp_files = generateTempFileSet(counter)
    temp_files_full = generateFullTempFileSet(temp_files)

    # copy files
    for key in model_set.keys():
        copyfile(model_set[key], temp_base_path + temp_files[key])
        replaceStrings.append((os.path.basename(model_set[key]), temp_files[key]))

    # because we did exchange the repository we need to adjust the number
    replaceStrings.append(("repository_" + model_num_else + ".repository", temp_files["repository"]))

    for file_key in temp_files_full:
        with open(temp_files_full[file_key], 'r') as file:
            filedata = file.read()
        for to_replace in replaceStrings:
            filedata = filedata.replace(to_replace[0], to_replace[1])
        with open(temp_files_full[file_key], 'w') as file:
            file.write(filedata)

    return temp_files_full


# In[7]:


def generateModelSet(model_base_path, model_num_repository, model_num_else, id):
    current = {
        "repository": model_base_path + "repository_" + model_num_repository + ".repository",
        "system": model_base_path + "system_" + model_num_else + ".system",
        "resenv": model_base_path + "resourceenv_" + model_num_else + ".resourceenvironment",
        "allocation": model_base_path + "allocation_" + model_num_else + ".allocation",
        "usage": model_base_path + "usage_" + model_num_else + ".usagemodel"
    }
    return transformClosureTemporary(current, model_num_else, id)


# In[8]:


def doBlockingSimulation(model_set, pcm_client):
    pcm_client.clearSimulations()
    simulator = pcm_client.prepareSimulation()
    simulator.setConfiguration(simulation_config)
    simulator.setModelSet(model_set)

    simulator.startSimulation()
    while not simulator.isFinished():
        time.sleep(0.25)
    time.sleep(0.5)

    return json.loads(simulator.getResults())


# In[9]:


def generateAnalysisData(model_set, service_name, required_measurements, pcm_client):
    res = []
    zero_max = 3

    while (len(res) < required_measurements) and zero_max > 0:
        size_bef = len(res)
        data = doBlockingSimulation(model_set, pcm_client)
        if "values" in data:
            for value in data["values"]:
                point = value["key"]["point"]
                if (point["type"] == "ENTRY_LEVEL_CALL") and (service_name in point["stringRepresentation"]):
                    measures = value["value"][1]["measures"]
                    for meas in measures:
                        if (len(res) < required_measurements):
                            res.append(float(meas["v"]["v"]))
        step = len(res) - size_bef
        if step == 0:
            zero_max -= 1

    return res


def enrichMonitoringData(monitoring, needed_size):
    add = []
    while len(monitoring) + len(add) < needed_size and len(monitoring) > 0:
        add.append(random.choice(monitoring))
    monitoring.extend(add)

lock = threading.Lock()
def generateSingleModelPrediction(model_base_path, exec_num, model_num, z, id):
    global lock, unoccupied_clients
    with lock:
        chosen_unocc = random.choice(unoccupied_clients)
        unoccupied_clients.remove(chosen_unocc)

    models = generateModelSet(model_base_path, str(model_num + 1), str(z + 1), id)
   
    if observed_service in monitoring_files[exec_num][z].keys():
    	monitoring_current = monitoring_files[exec_num][z][observed_service]
    else:
    	monitoring_current = []
    
    analysis_current = generateAnalysisData(models, observed_service_name, len(monitoring_current),
                                            pcm_clients[chosen_unocc])

    with lock:
        unoccupied_clients.append(chosen_unocc)

    return [monitoring_current, analysis_current]


# In[10]:


# backward prediction
def backwardPrediction(model_base_path, exec_num, model_num, rel_size=0):
    analysis = []
    monitoring = []

    futures = []
    for z in range(model_num - 1, -1, -1):
        futures.append(
            pcm_process_pool.submit(generateSingleModelPrediction, model_base_path, exec_num, model_num, z, z))

    # add all collected values together
    for f in concurrent.futures.as_completed(futures):
        monitoring.extend(f.result()[0])
        analysis.extend(f.result()[1])

    return (monitoring, analysis)


# In[11]:


def forwardPrediction(model_base_path, exec_num, model_num, rel_size=0):
    analysis = []
    monitoring = []

    futures = []
    for z in range(model_num + 1, len(monitoring_files[exec_num])):
        futures.append(
            pcm_process_pool.submit(generateSingleModelPrediction, model_base_path, exec_num, model_num, z, z))

    # add all collected values together
    for f in concurrent.futures.as_completed(futures):
        monitoring.extend(f.result()[0])
        analysis.extend(f.result()[1])

    return (monitoring, analysis)


# In[12]:


evaluation_results_forward = {"iterations": []}

def doForwardPrediction():
    for k in tqdm(data_nums):
        print("Start iteration " + str(k))
    
        model_base_path = data_base_path + data_prefix + str(k) + "/" + data_model_path + "/"

        iteration_data = {}

        ws_forward = []
        ks_forward = []
        mean_dist_forward = []

        rel_size = None
        for i in tqdm(range(cut_size_front, len(monitoring_files[k - 1]) - cut_size_end)):
            values_tuple = forwardPrediction(model_base_path, k - 1, i, 0 if rel_size is None else rel_size)
            if rel_size is None:
                rel_size = len(values_tuple[0])

            ws_forward.append(wasserstein_distance(values_tuple[0], values_tuple[1]))
            ks_forward.append(stats.ks_2samp(values_tuple[0], values_tuple[1])[0])
            mean_dist_forward.append(abs(statistics.mean(values_tuple[0]) - statistics.mean(values_tuple[1])))

        # prepare iteration data
        iteration_data["ws"] = ws_forward
        iteration_data["ks"] = ks_forward
        iteration_data["md"] = mean_dist_forward

        # set whole iteration data
        evaluation_results_forward["iterations"].append(iteration_data)


# In[13]:


evaluation_results_backward = {"iterations": []}

def doBackwardPrediction():
    for k in tqdm(data_nums):
        print("Start iteration " + str(k))

        model_base_path = data_base_path + data_prefix + str(k) + "/" + data_model_path + "/"

        iteration_data = {}

        ws_backward = []
        ks_backward = []
        mean_dist_backward = []

        rel_size = None
        for i in tqdm(range(len(monitoring_files[k - 1]) - cut_size_end - 1, cut_size_front - 1, -1)):
            values_tuple = backwardPrediction(model_base_path, k - 1, i, 0 if rel_size is None else rel_size)
            
            if rel_size is None:
                rel_size = len(values_tuple[0])

            ws_backward.append(wasserstein_distance(values_tuple[0], values_tuple[1]))
            ks_backward.append(stats.ks_2samp(values_tuple[0], values_tuple[1])[0])
            mean_dist_backward.append(abs(statistics.mean(values_tuple[0]) - statistics.mean(values_tuple[1])))

        # prepare iteration data
        iteration_data["ws"] = ws_backward
        iteration_data["ks"] = ks_backward
        iteration_data["md"] = mean_dist_backward

        # set whole iteration data
        evaluation_results_backward["iterations"].append(iteration_data)


doBackwardPrediction()
with open('results_backward_raw.json', 'w') as outfile:
    json.dump(evaluation_results_backward, outfile)


doForwardPrediction()
with open('results_forward_raw.json', 'w') as outfile:
    json.dump(evaluation_results_forward, outfile)