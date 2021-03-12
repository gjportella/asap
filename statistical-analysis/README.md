# ASAP - Statistical Analysis

Statistical analysis and experimental results of AWS/EC2 spot instances.

## Installation

The installation prerequisites are as follows:

* Java 8 (OpenJDK or Oracle JDK) to compile and run code in the Java language.
* Maven 3 to resolve and download library dependencies in the build process, as well as package the compiled code and run the test cases.

## Launch

To launch the statistical analysis, please follow the steps below (under Linux):

1. Install Java 8 and Maven 3 in your development environment.
2. Download this repository or clone it using a Git client. If you choose to use a Git client, please run the following command:
```
git clone https://github.com/gjportella/asap
```

3. Change to the ASAP - Statistical Analysis project path:
```
cd asap/statistical-analysis
```

4. Compile the project using Maven 3 (further information about Maven targets and the Build Lifecycle may be found at https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html):
```
mvn compile
```

5. Run unit test case:
```
mvn test
```

At this step, the test case will call the end point class br.unb.cic.boot.BootCCPEAnalysis, in which the statistical analyzes referring to the historical price data of c3.8xlarge, g2.2xlarge, m4.10xlarge, r3.2xlarge and t1.micro spot instance types, during the months from September to November 2016, are performed.

## Building an Analysis

To build a new custom analysis, you may use one of the following end point examples in package [br.unb.cic.boot](https://github.com/gjportella/asap/tree/main/statistical-analysis/src/main/java/br/unb/cic/boot) under the src/main/java path:

* BootCCPEAnalysis.java: statistical analysis and experimental results detailed in CCPE 2017 (for more details, please click [here](https://github.com/gjportella/asap)).
* BootOnDemandAnalysis.java: statistical analysis and experimental results detailed in WSCAD 2016 (for more details, please click [here](https://github.com/gjportella/asap)).
* BootSpotAnalysis.java: monthly analysis of spot instance types from September to December 2016.

The price variation history files are placed at src/main/resources. In this path you will also find the analysis output files (text reports and gnuplot scripts).
