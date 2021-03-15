# ASAP - Statistical Analysis

AWS/EC2 on demand and spot pricing statistical analysis.

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

At this step, the test case will call the end point class [br.unb.cic.boot.BootCCPEAnalysis](https://github.com/gjportella/asap/blob/main/statistical-analysis/src/main/java/br/unb/cic/boot/BootCCPEAnalysis.java), in which the statistical analyses referring to the historical price data of c3.8xlarge, g2.2xlarge, m4.10xlarge, r3.2xlarge and t1.micro spot instance types, during the months from September to November 2016, are performed.

## Building a New Analysis

To build a new custom analysis, you may use one of the following end point examples in package [br.unb.cic.boot](https://github.com/gjportella/asap/tree/main/statistical-analysis/src/main/java/br/unb/cic/boot), under the src/main/java path:

* [BootCCPEAnalysis.java](https://github.com/gjportella/asap/blob/main/statistical-analysis/src/main/java/br/unb/cic/boot/BootCCPEAnalysis.java): statistical analysis and experimental results detailed in CCPE 2017 (for more details, please click [here](https://github.com/gjportella/asap)).
* [BootOnDemandAnalysis.java](https://github.com/gjportella/asap/blob/main/statistical-analysis/src/main/java/br/unb/cic/boot/BootOnDemandAnalysis.java): statistical analysis and experimental results detailed in WSCAD 2016 (for more details, please click [here](https://github.com/gjportella/asap)).
* [BootSpotAnalysis.java](https://github.com/gjportella/asap/blob/main/statistical-analysis/src/main/java/br/unb/cic/boot/BootSpotAnalysis.java): monthly analysis of spot instance types from September to December 2016.

## History Files

The spot price variation history files are placed at [src/main/resources](https://github.com/gjportella/asap/tree/main/statistical-analysis/src/main/resources). You can get the history files using the ec2dsph command using AWS-CLI. For more information, please visit [describe-spot-price-history](http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-spot-price-history.html) command reference and [AWS Command Line Interface](http://docs.aws.amazon.com/cli/latest/userguide/tutorial-ec2-ubuntu.html) documentation.

## Output Files

In [src/main/resources](https://github.com/gjportella/asap/tree/main/statistical-analysis/src/main/resources) path you will also find the analysis output files (text reports, PNG images and gnuplot scripts). All files have a prefix defined by the instance type and/or period of analysis (e.g. t1.micro_2016-11, in which t1.micro refers to the instance type and 2016-11 refers to the periodo of November 2016).

The output files for the on demand analysis are:

* \[file prefix\]-frequency-distribution.png
* \[file prefix\]-homoscedasticity.png
* \[file prefix\]-scatterplot.png
* \[file prefix\]-scatterplot.txt
* \[file prefix\]-statistics.txt

THe output files for the spot analysis are:

* \[file prefix\]-scatterplot.txt-gnuplot-script.txt
* \[file prefix\]-scatterplot.txt-gnuplot-script.png (generated from the execution of the script in gnuplot)
