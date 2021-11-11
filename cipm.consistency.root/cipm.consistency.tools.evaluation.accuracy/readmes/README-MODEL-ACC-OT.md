## Model Accuracy of the System/Environment/Allocation Model at Operation-Time
Our approach is capable of updating the system, environment & allocation model of the PCM at operation-time by collecting and processing monitoring data. The evaluation is based on the execution and orchestration of change scenarios that are executed at operation-time within a case study application. Here, we used TeaStore, as it is designed in a flexible and dynamic way and allows to apply changes to the system/environment/allocation easily.

We generated ten exemplary sequences of changes (so called scenarios), which consist of (de-)replications, (de-)allocations, migrations, system composition changes and workload changes. In the course of generating the scenarios, we also derived reference models that represent the desired results. Afterwards, we applied the scenarios and our approach managed to update the system/environment/allocation models for different points in time. Finally, we compared the reference models with the models that resulted from the usage of our approach by using the Jaccard coefficient (JC). The comparison process is similar to the one that is described [here](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/readmes/README-MODEL-ACC-DT.md).

In the following, we first describe how you can execute the experiments (application of change scenarios, collection of monitoring data, ...). The first step can be skipped, as it is very time-consuming and we provide the data that arose during the execution of the experiments in our environment. Second we introduce how the JC values can be calculated from the experiment data and finally, we provide the results that were obtained from the second step in our setup.

### 1. Gather Experiment Data
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

Please follow this [link](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.docker/teastore/README.md) to access information on how to perform the experiment.

### 2. Calculate Metrics
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

1. Execute the "ModelAccuracyEvaluationTeaStore" class, which is located in the "cipm.consistency.tools.evaluation.accuracy.models.opstime" package. The corresponding project is named "cipm.consistency.tools.evaluation.accuracy".
2. After the updated models from the experiment results and the reference models were compared, the resulting Jaccard coefficients (JC) are printed and should be equal to 1.0 (which means that the reference models and the extracted models are equal in all cases).

### 3. Summarized Results

| Change Type | System | Allocation | Resource Environment |
| ----------- |:-------|:----------:|---------------------:|
| (De-)/Allocation | 1.0    | 1.0        |    1.0               |
| (De-)/Replication | 1.0    | 1.0        |     1.0              |
| Migration      | 1.0    | 1.0        |    1.0               |
| System Composition    | 1.0    | 1.0        |     1.0              |
| Workload      | 1.0    | 1.0        |    1.0               |