# CIPM Evaluation - TeaStore Case Study
We performed several experiments for the evaluation of our approach. A list with more details is provided [here](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Evaluation). Here, it is described how the experiments that involve the [TeaStore case study](https://github.com/dmonsch/TeaStore) can be reproduced.

There are two main experiments: the first one monitors the TeaStore application and applies a certain workload, i.e. there are no dynamic changes at operation-time (Exp.1) and the second experiment considers changes that are executed while monitoring the application (Exp.2). The type of changes that were considered are described [here]((https://github.com/CIPM-tools/CIPM-Pipeline/blob/master/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/readmes/README-MODEL-ACC-OT.md)).

### Setup using Docker
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

We provide Docker files and setup scripts that can be used to easily setup and execute the experiments. Please perform the following steps to reproduce the experiments:
1. It is necessary to build the whole project and execute a Gradle task that is responsible for setting up all files that are required for the Docker containers. To do so, create a new Gradle task within the run configurations of your project and execute the task "prepareDocker" (project context should be "cipm.consistency.root").
2. Build the Docker images by executing the scripts "build-cipm-images.[sh/bat]" and "build-teastore-images.[sh/bat]".
3. Start the experiment you want to carry out by using the scripts:
	1. "execute-without-change-scenarios.[sh/bat]" for Exp.1
	2. "execute-with-change-scenarios.[sh/bat]" for Exp.2 **IMPORTANT**: Because change scenarios are executed at operation-time which can lead to the creation/deletion of new containers etc., this experiment has a high resource consumption (e.g., 30 GB of RAM).
4. The data that resulted from the experiment execution is saved within the "teastore/experiment-results" folder.