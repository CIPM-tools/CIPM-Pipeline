# Scalability Evaluation

We evaluated the scalability of the transformations within the implemented pipeline comprehensively. First, we identified interesting scenarios in terms of their impact on the execution time (especially worst-case scenarios). Next, we synthetically generated monitoring data and used it as input for the transformations. In most cases we generate unrealistically high amounts to get more to obtain more meaningful results. For the transformations that were taken over from existing approaches (iObserve), we showed that the results are consistent. This is important, because we use different monitoring data structures and it is not obvious that the scalability is preserved.

## Perform the Evaluation
1. Setup the project using Eclipse together with Gradle (read the instructions here [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
2. Execute the scalability tests which are stored in the sub packages of *cipm.consistency.tools.evaluation.scalability*:
    - RepositoryUpdateScalabilityTest (T<sub>RepositoryCalibration</sub>)
    - ResourceEnvironmentScalabilityTest (T<sub>ResourceEnvironment</sub>)
    - UsageModelTransformationScalabilityTest (T<sub>UsageExtraction</sub>)
    - SystemAllocationUpdateScalabilityTest (T<sub>SystemComposition</sub>)
3. Inspect the results which are stored in the scalability project folder under "test-results/scalability". These contain a line chart for each experiment and the raw results formatted as JSON.

## Evaluation Results
This section contains the visualized results of all scalability tests. Executed on a MacBook Pro (13-inch, 2017, 3,1 GHz Dual-Core Intel Core i5). 

### T<sub>RepositoryCalibration</sub>
We distinguish between two scenarios:
1. Calibration of **one** stochastic expression with increasing monitoring data amount (internal scalability).
2. Calibration of an increasing amount of stochastic expressions with constant monitoring data amount (overall scalability).

#### Scenario 1 (Internal Scalability)
![Internal Scalability](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_repository_internal.png)

&#8594; scales **linearly** for one stochastic expression that is calibrated and increasing amount of monitoring data

#### Scenario 2 (Overall Scalability)

![Overall Scalability](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_repository_overall.png)

&#8594; scales **linearly** for an increasing amount of stochastic expressions that should be calibrated and constant amount of monitoring data

### T<sub>ResourceEnvironment</sub>
We distinguish between two scenarios:
1. An increasing number of new hosts occur, each with a maximum of one connection to another host (n hosts, 1 link each) - **sparse**
2. An increasing number of new hosts occur, each with a connection to all other hosts (n hosts, n links each) - **fully-meshed**

#### Scenario 1 (sparse):
![Sparse Scalability](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_resourceenv_sparse.png)

&#8594; scales **linearly** for an increasing amount of upcoming hosts

#### Scenario 2 (fully meshed)
![Fully Meshed Scalability](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_resourceenv_fullymeshed.png)

&#8594; **exponential** rise of the execution times, due to the exponentially increasing number of connections

### T<sub>UsageExtraction</sub>
We distinguished between two scenarios:
1. An increasing number of users execute one action each.
2. **One** user executes an increasing number of different actions.

#### Scenario 1 (n users, 1 action)
![Usage Scalability Users](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_1user1action.png)

&#8594; execution times scale **linearly** with an increasing amount of different users

#### Scenario 2 (1 user, n actions)
![Usage Scalability Actions](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_1userNactions.png)

&#8594; Please note that both axes are scaled logarithmically. **Exponential** rise of the execution times, but adequate results for realistic scenarios. The results are consistent with the ones that were obtained in the context of iObserve (see [Heinrich, 2020](https://www.sciencedirect.com/science/article/abs/pii/S016412122030159X)).

### T<sub>SystemComposition</sub>
We observed the execution times of the transformation with an increasing amount of occurring changes of the system composition.

![System Allocation Scalability](https://raw.githubusercontent.com/CIPM-tools/CIPM-Pipeline/e5d2da70421410275337b29192ab6ea776a9b524/cipm.consistency.root/cipm.consistency.tools.evaluation.scalability/test-results/scalability/scalability_system_allocation.png)

&#8594; scales **sublinearly** until around 1500 number of system composition changes and scales **linearly** for > 1500 changes &#8594; extremely low execution times, even for unrealistcally high number of considered changes