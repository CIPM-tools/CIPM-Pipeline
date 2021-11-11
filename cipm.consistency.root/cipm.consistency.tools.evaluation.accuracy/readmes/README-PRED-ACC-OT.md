## Prediction Accuracy of the System/Environment/Allocation Model at Operation-Time
Our approach is capable of updating all parts of the PCM at operation-time by collecting and processing monitoring data. In addition to the evaluation of the model accuracy (described [here](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/readmes/README-MODEL-ACC-OT.md), we also analyzed whether the performance predictions that result from the simulation of the updated models, are consistent with what we see in the monitoring data. The experiment setup is the same as [before](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/readmes/README-MODEL-ACC-OT.md). We used the TeaStore case study and executed change scenarios at operation-time.

In the following, we first describe the evaluation data that we want to collect. Second, we introduce how the experiments can be reproduced (application of change scenarios, collection of monitoring data, ...). The second step can be skipped, as it is very time-consuming and we provide the data that arose during the execution of the experiments in our environment. Third, we explain how the metrics can be calculated, and finally, we provide the results that were obtained from the third step in our setup.

### 1. Experiment Strategy
[Enabling Consistency between Software Artefacts for Software Adaption and Evolution](https://ieeexplore.ieee.org/document/9426765):

1. Execution of the experiment, storage of the derived models and the associated monitoring data.
2. Examination of the models at different points in time based on simulations.
3. Comparison of the simulation results with the monitoring data in two different ways:
	1. Comparison with monitoring data collected *after* the construction of the model under consideration (forward prediction). This allows us to make statements about how well the derived model can be used to predict future scenarios.
	2. Comparison with monitoring data that was collected chronologically *before* the construction of the model under consideration (backward prediction). In this way, it can be determined how well the model is able to reproduce previously observed situations.


### 2. Gather Experiment Data
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

Please follow this [link](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.docker/teastore/README.md) to access information on how to perform the experiment.

### 3. Calculate Metrics
***
**IMPORTANT**
* Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
* Requires the setup of at least one headless simulator for PCM models (see [Setup PCM-Headless](https://github.com/dmonsch/PCM-Headless/wiki/Setup-using-Docker)).

***

We built a Python script that performs the simulations and the metric calculations, as there is no efficient implementation for calculating the [Wasserstein distance](https://en.wikipedia.org/wiki/Wasserstein_metric) available for Java yet.

The script is located at "scripts/evaluation_accuracy_ot.py" within the "cipm.consistency.tools.evaluation.accuracy" project. Depending on the number of simulators you are running and on the ports that they are running on, adjust the lines 36-37 (we used 4 simulator instances):```pcm_clients = [HeadlessPCM("http://127.0.0.1:8080"), HeadlessPCM("http://127.0.0.1:8090"),HeadlessPCM("http://127.0.0.1:8100"), HeadlessPCM("http://127.0.0.1:8110")]``` accordingly. We used Python 3.7 and please do not forget do install the necessary packages:
```
import concurrent
import requests
import json
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
```

Afterwards, you can simply execute the script within your Python environment and wait for it to finish. The results are located in the same folder as the script.

### 4. Summarized Results
Because the simulations of the models are depending on the sample process of stochastic expressions, every execution of the script provides different results, but the shape of the curves always looks similar, as follows:

![Wasserstein distance over time, exemplary](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/readmes/exemplary/ws_ot.png?raw=true)