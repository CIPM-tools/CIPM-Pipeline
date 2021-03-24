This project is part of the Continuous Integration of Performance Models (CIPM) approach and enables consistency between system design and adaptive changes as well as evolutionary changes.

## Overview
The following features are supported by CIPM:
* Instrumentation of a Java application to establish monitoring by means of [Kieker](http://kieker-monitoring.net/)
* Incremental calibration of architectural Performance Models PMs.
* Consistency preservation between a Palladio Component Model (PCM) instance (as architectural PMs) and source code (focus on System Model)
* Consistency preservation between a Palladio Component Model (PCM) instance and a running software system (All model parts supported)

The code uses [Gradle](https://gradle.org/) as building tool and was developed with version 13 of the JDK. In this Wiki we provide general information about the structure and the development setup of the Gradle projects and how the evaluation experiments can be carried out.

## Documentation
A detailed documentation for setting up and using this approach can be found  in [Wiki](https://github.com/CIPM-tools/CIPM-Pipeline/wiki). 

## Related Publications
**2021**: David Monschein, Manar Mazkatli, Robert Heinrich, and Anne Koziolek; [Enabling consistency between software artefacts for software adaption and evolution]( https://sdqweb.ipd.kit.edu/publications/pdfs/Monschein_ICSA21.pdf).

**2020**: Manar Mazkatli, David Monschein, Johannes Grohmann, and Anne Koziolek; [Incremental calibration of architectural performance models with parametric dependencies]( https://sdqweb.ipd.kit.edu/publications/pdfs/mazkatli2020a.pdf).

**2018**: Manar Mazkatli and Anne Koziolek; [Continuous integration of performance model]( https://sdqweb.ipd.kit.edu/publications/pdfs/Mazkatli2018Qudos1.pdf).
