# ASAP - LSTM Analysis

AWS/EC2 spot pricing analysis using Long Short-Term Memory (LSTM) neural networks.
The LSTM neural network implementation is based on the [Deep Learning for Java (DL4J)](https://deeplearning4j.org/) framework.

## Installation

The installation prerequisites are as follows:

* Java 8 (OpenJDK or Oracle JDK) to compile and run code in the Java language.
* Maven 3 to resolve and download library dependencies in the build process, as well as to package the compiled code and to run the test cases.

## Launch

To launch the LSTM analysis, please follow the steps below (under Linux):

1. Install Java 8 and Maven 3 in your development environment.
2. Download this repository or clone it using a Git client. If you choose to use a Git client, please run the following command:
```
git clone https://github.com/gjportella/asap
```

3. Change to the ASAP - LSTM Analysis project path:
```
cd asap/lstm-analysis
```

4. Compile the project using Maven 3 (further information about Maven targets and the Build Lifecycle may be found at https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html):
```
mvn compile
```

5. Run unit test case:
```
mvn test
```

At this step, the unit test case [br.unb.cic.laico.test.TestCaseForSingleInstance](https://github.com/gjportella/asap/blob/main/lstm-analysis/src/test/java/br/unb/cic/laico/test/TestCaseForSingleInstance.java) is executed. It runs the end point class [br.unb.cic.laico.boot.BootSingleInstanceAnalysis](https://github.com/gjportella/asap/blob/main/lstm-analysis/src/main/java/br/unb/cic/laico/boot/BootSingleInstanceAnalysis.java), in which a prediction analyses of spot prices for the c5n.2xlarge instance type, during the month of April 2020, is performed.

## Building a New Analysis

To build a new custom analysis, you may use one of the following end point examples in package [br.unb.cic.laico.boot](https://github.com/gjportella/asap/tree/main/lstm-analysis/src/main/java/br/unb/cic/laico/boot), under the [asap/lstm-analysis/src/](https://github.com/gjportella/asap/tree/main/lstm-analysis/src) path:

* [BootCaseStudyAnalysis.java](https://github.com/gjportella/asap/blob/main/lstm-analysis/src/main/java/br/unb/cic/laico/boot/BootCaseStudyAnalysis.java): LSTM and utility-based analysis and case studies detailed in \[4\] (for more details, please click [here](https://github.com/gjportella/asap)).
* [BootIExtensiveAnalysis.java](https://github.com/gjportella/asap/blob/main/lstm-analysis/src/main/java/br/unb/cic/laico/boot/BootIExtensiveAnalysis.java): extensive LSTM and utility-based analysis and results for 8 instance types, from January to June 2020, also detailed in \[4\].
* [BootSingleInstanceAnalysis.java](https://github.com/gjportella/asap/blob/main/lstm-analysis/src/main/java/br/unb/cic/laico/boot/BootSingleInstanceAnalysis.java): c5n.2xlarge LSTM analysis during the month of April 2020, as mentioned before.

## History Files

The spot price variation history files are placed at [src/main/resources/](https://github.com/gjportella/asap/tree/main/lstm-analysis/src/main/resources). You can get the history files using the ec2dsph command using AWS-CLI. For more information, please visit [describe-spot-price-history](http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-spot-price-history.html) command reference and [AWS Command Line Interface](http://docs.aws.amazon.com/cli/latest/userguide/tutorial-ec2-ubuntu.html) documentation.

## Output Files

In [src/main/resources/](https://github.com/gjportella/asap/tree/main/lstm-analysis/src/main/resources) path you will also find the analysis output files (text reports and gnuplot scripts). All files have a prefix defined by the timestamp, LSTM configuration, instance type and availability zone (e.g. 20210125-162510-LSTM-A2H32E100L0.005T2-c5n.2xlarge-us-east-1c, in which 20210125-162510 is the timestamp, LSTM-A2H32E100L0.005T2 is the LSTM neural network configuration, c5n.2xlarge is the instance type and us-east-1c is the region/availability zone).

The output files are:

* \[file prefix\]-dl4j-testing.txt: pre-processed testing data for the DL4J framework.
* \[file prefix\]-dl4j-training.txt: pre-processed training data for the DL4J framework.
* \[file prefix\]-error-data.txt: error data for MSE estimation.
* \[file prefix\]-final-report.txt: detailed execution report.
* \[file prefix\]-gnuplot-predicted-data.txt: predicted data for gnuplot script.
* \[file prefix\]-gnuplot-testing-data.txt: testing data for gnuplot script.
* \[file prefix\]-gnuplot-training-data.txt: training data for gnuplot script.
* \[file prefix\]-gnuplot-script.txt: gnuplot script to generate the PNG image for the analysis period.
