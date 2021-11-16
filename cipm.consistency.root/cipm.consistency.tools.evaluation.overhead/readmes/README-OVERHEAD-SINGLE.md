## Monitoring Overhead for a single Service Execution

We measured the total monitoring overhead resulting from a single call of the "confirmOrder" service of the TeaStore case study. To do so, we mocked the service and extracted all monitoring operations that we introduced. Subsequently, we measured the execution time of these monitoring operations. Furthermore, we distinguished between the usage of a fine-grained monitoring and a coarse-grained monitoring (see our publications for details).

Here, we describe how the measurements can be performed and introduce the results that we obtained in our test environment. Please note that the concrete execution times depend on your machine, but the order of magnitude should be identical for conventional computers.

### 1. Perform overhead measurements
The execution of the measurements is very easy. Just execute the "EvaluationOverheadSingleMocked" class within the "cipm.consistency.tools.evaluation.overhead" package, which is located in the project named "cipm.consistency.tools.evaluation.overhead".

**IMPORTANT:** Sometimes the measurement throws exceptions and freezes within this state. If you experience this problem, please stop the measurement and execute it again. The exceptions are related to the network transfer of Kieker records and are somehow related to some threading stuff (probably because the server that receives the records runs in the same process).

### 2. Summarized results
The following listing shows the respective overheads in milliseconds (median) and the corresponding standard deviations:
* *Fine-grained:* 1.755ms (st. dev. = 0.389ms)
* *Coarse-grained:* 0.731ms (st. dev. = 0.144ms)