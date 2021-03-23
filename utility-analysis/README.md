# ASAP - Utility Analysis

AWS/EC2 spot pricing utility-based analysis.

## Installation

The installation prerequisites are as follows:

* Java 8 (OpenJDK or Oracle JDK) to compile and run code in the Java language.
* Maven 3 to resolve and download library dependencies in the build process, as well as package the compiled code and run the test cases.

## Launch

To launch the utility analysis, please follow the steps below (under Linux):

1. Install Java 8 and Maven 3 in your development environment.
2. Download this repository or clone it using a Git client. If you choose to use a Git client, please run the following command:
```
git clone https://github.com/gjportella/asap
```

3. Change to the ASAP - Utility Analysis project path:
```
cd asap/utility-analysis
```

4. Compile the project using Maven 3 (further information about Maven targets and the Build Lifecycle may be found at https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html):
```
mvn compile
```

5. Run unit test case:
```
mvn test
```

At this step, the test case will call the end point class [br.unb.cic.laico.boot.BootUtilitySingleInstanceAnalysis](./src/main/java/br/unb/cic/laico/boot/BootUtilitySingleInstanceAnalysis.java), in which the utility-based analyse for the m5.2xlarge instance types, during the month of January 2020, is performed.

## Building a New Analysis

To build a new custom analysis, you may use one of the following end point examples in package [br.unb.cic.laico.boot](./src/main/java/br/unb/cic/laico/boot), under the src/main/java path:

* [BootUtilityExtensiveAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootUtilityExtensiveAnalysis.java): monthly analysis of 5 spot instance types from September to December 2016.
* [BootUtilityIEEECloudAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootUtilityIEEECloudAnalysis.java): utility analysis and experimental results detailed in IEEE Cloud (for more details, please click [here](https://github.com/gjportella/asap)).
* [BootUtilitySingleInstanceAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootUtilitySingleInstanceAnalysis.java): single m5.2xlarge instance type analysis during january 2020.

## History Files

The spot price variation history files are placed at [src/main/resources](./src/main/resources). You can get the history files using the ec2dsph command using AWS-CLI. For more information, please visit [describe-spot-price-history](http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-spot-price-history.html) command reference and [AWS Command Line Interface](http://docs.aws.amazon.com/cli/latest/userguide/tutorial-ec2-ubuntu.html) documentation.

## Output Files

In [src/main/resources](./src/main/resources) path you will also find the analysis output files (text reports, PNG images and gnuplot scripts). All files have a prefix defined by a timestamp, instance type and availability zone.

The output files for the analysis are:

* \[file prefix\]_final-report.txt
* \[file prefix\]_gnuplot-bid-availability-script.txt
* \[file prefix\]_gnuplot-estimated-data.txt
* \[file prefix\]_gnuplot-future-window-script.txt
* \[file prefix\]_gnuplot-utility-script.txt
* \[file prefix\]_r-data.txt
* \[file prefix\]_suggestion-table.txt
