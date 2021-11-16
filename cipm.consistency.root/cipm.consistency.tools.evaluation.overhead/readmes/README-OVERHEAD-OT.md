## Cumulated Monitoring Overhead over Time at Operation-Time

We also analyzed the overall monitoring overhead over time. For this purpose, during the experiment in which we used the TeaStore (see [here](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.docker/teastore/README.md)), we also recorded the arising monitoring overhead. We want to show here that the adaptive monitoring (more details in our publications), can lead to a reduction of the overhead in the long run.

In the following, we first describe how data about the monitoring overhead in the TeaStore case study can be collected. The first step can be skipped, as it is very time-consuming and we provide the data that arose during the execution of the experiments in our environment. Second, we explain how the data can be analyzed/evaluated, and finally, we provide the results that were obtained from the second step in our setup.

### 1. Gather Experiment Data
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

Please follow this [link](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.docker/teastore/README.md) to access information on how to perform the experiment.

### 2. Calculate Results

We built a Python script that performs the analysis of the experiment data. It calculates the sum of the monitoring overhead for intervals of 5 minutes. The script is named "evaluation_overhead.py" and is located at the root folder of the "cipm.consistency.tools.evaluation.overhead" project. Simply execute it, get a plot of the results and the results are also saved in a file named "result.json".

### 3. Summarized Results
Cumulated monitoring overhead (5 minute intervals) plotted over time:

![Median of the arising monitoring overhead over time when considering five minute intervals, exemplary](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.overhead/readmes/exemplary/overhead_ot.png?raw=true)

After the validation process begins to find individual services that are well represented in the model, the granularity of monitoring is reduced. This happens after about 20 minutes.