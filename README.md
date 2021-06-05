# job-processing-system

## Pre-requisites
`java`  and `mvn` should be installed.


## Config
1. Clone this repo, and cd to the project dircetory.
2. Edit the content of `src/main/resources/job_processor.properties` as per your preference. You may only want to edit the path of the files. 
There are two file paths of interest: 
* The local file that the individual jobs read and write to. It is given by : `job.execution.io.path` property. 
This file has been used to simulate IO calls during execution of the jobs.
* The output file that contains the job statistics over a period of time. It is given by : `job.tracker.output.path` property.

## Build
Execute `mvn clean;mvn package` to build a uber jar.

## Run
From the project dircetory, run the below command:
`java -classpath ./target/job-processor-0.0.1-SNAPSHOT.jar bharati.binita.job.processor.Main &`

