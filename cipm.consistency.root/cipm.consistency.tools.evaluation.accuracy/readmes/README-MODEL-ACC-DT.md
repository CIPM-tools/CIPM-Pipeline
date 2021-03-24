## Model Accuracy of the System Model Extraction at Development-Time
We developed an approach which is able to extract an up-to-date System Model from source-code and an existing Repository Model. We evaluated if the extracted models are correct by comparing them to reference models and calculating the Jaccard coefficient (JC). Please read our paper if you want to learn more about this procedure.

In the following, we describe how you can execute the System Model extraction process and how you can compare the results to reference models. Because the setup and collection of the data requires some effort, you can also skip step 1 (gathering the extracted models) and directly calculate the results by means of the data that we collected upfront. Furthermore, if you are just interested in the results, you can directly jump to point 3.

### 1. Gather Extracted Models
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle).
***


CoCoME: CashDeskConnectorIf -> None select -> Select existing assembly

### 2. Calculate Metrics
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle).
***

### 3. Summarized Results

| Casestudy | Jaccard Coefficient (JC)| Conflicts |
| --------- |:-----------------------:|:---------:|
| CoCoME    | 1.0                     |       2   |
| TeaStore  | 1.0                     |       5   |