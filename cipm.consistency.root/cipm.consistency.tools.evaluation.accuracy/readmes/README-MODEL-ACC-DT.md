## Model Accuracy of the System Model Extraction at Development-Time
We developed an approach which is able to extract an up-to-date System Model from source-code and an existing Repository Model. We evaluated if the extracted models are correct by comparing them to reference models and calculating the Jaccard coefficient (JC). Please read our [paper](https://ieeexplore.ieee.org/document/9426765) if you want to learn more about this procedure.

In the following, we describe how you can execute the System Model extraction process and how you can compare the results to reference models. Because the setup and collection of the data requires some effort, you can also skip step 1 (gathering the extracted models) and directly calculate the results by means of the data that we collected upfront (1.1.). Furthermore, if you are just interested in the results, you can directly jump to point 3.

### 1. Gather Extracted Models
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***

* CoCoME
    1. Use the "Start Application CoCoME" launch configuration for Eclipse to start the web UI (located in the "cipm.consistency.app.start" project).
    2. When the startup is completed, navigate to the landing page of the web UI at [http://127.0.0.1:8080/](http://127.0.0.1:8080/).
    3. In the menu bar on the left, navigate to Design-Time > System Extraction.
    4. Click "Start System Model Building Procedure".
    5. Now click through the process and resolve the upcoming conflicts.
    6. The correct resolution requires domain knowledge and is as follows:
        1. Select "CashDeskConnectorIf" as interface that should be provided by the system as a whole.
        2. Do not select the proposed Assembly Context as solution, instead directly click on "Resolve Conflict".
        3. Select the proposed Assembly Context and finish the procedure.
    7. The extracted System Model is located in the "cipm.consistency.app.start" project under "models > cocome > cocome.system" (the old one gets overwritten).
* TeaStore
    1. Use the "Start Application TeaStore" launch configuration for Eclipse to start the web UI (located in the "cipm.consistency.app.start" project).
    2. When the startup is completed, navigate to the landing page of the web UI at [http://127.0.0.1:8080/](http://127.0.0.1:8080/).
    3. In the menu bar on the left, navigate to Design-Time > System Extraction.
    4. Click "Start System Model Building Procedure".
    5. Now click through the process and resolve the upcoming conflicts.
    6. The correct resolution requires domain knowledge and is as follows:
        1. Select "CartActions" and "ProductActions" as interfaces that should be provided by the system as a whole.
        2. Select the proposed Assembly Context **two** times.
        3. Select "Persistence" as component type.
        4. Select "ImageProvider" as component type and finish the procedure.
    7. The extracted System Model is located in the "cipm.consistency.app.start" project under "models > teastore > teastore.system" (the old one gets overwritten).

## 1.1. Raw data
The models that we extracted using our proposed procedure and which formed the basis for 2. can be found [here](https://github.com/CIPM-tools/CIPM-Pipeline/tree/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/test-data/devtime-system-extraction).

### 2. Calculate Metrics
***
**IMPORTANT**
Requires an error-free setup of our Gradle project (see [Setup using Gradle](https://github.com/CIPM-tools/CIPM-Pipeline/wiki/Setup-using-Gradle))
***
0. If you want to use System Models that you extracted on your own by following the instructions above, please copy them into the folder "cipm.consistency.tools.evaluation.accuracy > test-data > devtime-system-extraction" within the parent Gradle project. Name them "cocome-extracted.system" or "teastore-extracted.system", according to the considered case study. **But** you can also directly continue with step 1, as we already provide the models that we extracted using the procedure described above.
1. Execute the "SystemExtractionEvaluationCoCoME" class and/or the "SystemExtractionEvaluationTeaStore" class, which are both located in the "cipm.consistency.tools.evaluation.accuracy.models.devtime" package.
2. After the extracted model and the reference model of the respective case study were compared ([Script CoCoME](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/src/main/java/cipm/consistency/tools/evaluation/accuracy/models/devtime/SystemExtractionEvaluationCoCoME.java), [Script TeaStore](https://github.com/CIPM-tools/CIPM-Pipeline/blob/documentation/cipm.consistency.root/cipm.consistency.tools.evaluation.accuracy/src/main/java/cipm/consistency/tools/evaluation/accuracy/models/devtime/SystemExtractionEvaluationTeaStore.java)), the resulting Jaccard coefficient (JC) is printed and should be equal to 1.0 (which means that the reference model and the extracted model are equal).

### 3. Summarized Results
The column *Conflicts* quantifies the amount of manual interventions that were necessary to resolve ambiguities which occur at development time. For example, when inheritance is used, it is not certain which concrete class is used at operation time. This is explained in more detail in our [paper](https://ieeexplore.ieee.org/document/9426765).

| Casestudy | Jaccard Coefficient (JC)| Conflicts |
| --------- |:-----------------------:|:---------:|
| CoCoME    | 1.0                     |       2   |
| TeaStore  | 1.0                     |       5   |