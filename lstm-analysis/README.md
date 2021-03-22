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

At this step, the unit test case [br.unb.cic.laico.test.TestCaseForSingleInstance](./src/test/java/br/unb/cic/laico/test/TestCaseForSingleInstance.java) is executed. It runs the end point class [br.unb.cic.laico.boot.BootSingleInstanceAnalysis](./src/main/java/br/unb/cic/laico/boot/BootSingleInstanceAnalysis.java), in which a prediction analyses of spot prices for the c5n.2xlarge instance type, during the month of April 2020, is performed.

## Building a New Analysis

To build a new custom analysis, you may use one of the following end point examples in package [br.unb.cic.laico.boot](./src/main/java/br/unb/cic/laico/boot), under the [asap/lstm-analysis/src](./src) path:

* [BootCaseStudyAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootCaseStudyAnalysis.java): LSTM and utility-based analysis and case studies detailed in \[4\] (for more details, please click [here](https://github.com/gjportella/asap)).
* [BootIExtensiveAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootIExtensiveAnalysis.java): extensive LSTM and utility-based analysis and results for 8 instance types, from January to June 2020, also detailed in \[4\].
* [BootSingleInstanceAnalysis.java](./src/main/java/br/unb/cic/laico/boot/BootSingleInstanceAnalysis.java): c5n.2xlarge LSTM analysis during the month of April 2020, as mentioned before.

## History Files

The spot price variation history files are placed at [src/main/resources](./src/main/resources). You can get the history files with the ec2dsph command using AWS-CLI. For more information, please visit [describe-spot-price-history](http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-spot-price-history.html) command reference and [AWS Command Line Interface](http://docs.aws.amazon.com/cli/latest/userguide/tutorial-ec2-ubuntu.html) documentation.

## Output Files

In [src/main/resources](./src/main/resources) path you will also find the analysis output files (text reports, data files and gnuplot script). All files have a prefix defined by a timestamp, LSTM configuration, instance type and availability zone (e.g. 20210125-162510-LSTM-A2H32E100L0.005T2-c5n.2xlarge-us-east-1c, in which 20210125-162510 is the timestamp, LSTM-A2H32E100L0.005T2 is the LSTM neural network configuration, c5n.2xlarge is the instance type and us-east-1c is the region/availability zone).

The output files are:

* \[file prefix\]-dl4j-testing.txt: pre-processed testing data for the DL4J framework.
* \[file prefix\]-dl4j-training.txt: pre-processed training data for the DL4J framework.
* \[file prefix\]-error-data.txt: error data for MSE estimation.
* \[file prefix\]-final-report.txt: detailed execution report.
* \[file prefix\]-gnuplot-predicted-data.txt: predicted data for gnuplot script.
* \[file prefix\]-gnuplot-testing-data.txt: testing data for gnuplot script.
* \[file prefix\]-gnuplot-training-data.txt: training data for gnuplot script.
* \[file prefix\]-gnuplot-script.txt: gnuplot script to generate the PNG image for the analysis period.

## LSTM Configuration

The LSTM network configuration parameters are managed by the [LSTMConfiguration.java](./src/main/java/br/unb/cic/laico/analysis/lstm/config/LSTMConfiguration.java) class located in the [br.unb.cic.laico.analysis.lstm.config](./src/main/java/br/unb/cic/laico/analysis/lstm/config) package. These parameters include the definition of the network architecture layout, the hyperparameters (or learning parameters) and other specific parameters to handle, for example, the history file names and path.

To use the configuration class, import the corresponding package/class and instantiate an object as follows:

```
package simple.analysis;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;

public class MyAnalysis {

	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.ENGLISH);

		LSTMConfiguration config = new LSTMConfiguration();
		// configuration to be continued...

	}
}
```

### Network Architecture Layout

There are three network architecture layouts available: basic, intermediate and sophisticated. The first one is composed of just one LSTM layer (type 1). The second is built on two fully connected LSTM layers (type 2). The third layout has two LSTM layers and one additional dense layer (type 3). These architecture layouts are shown in the following images.

* [Basic layout](../images/basic_layout.png?raw=true)
* [Intermediate layout](../images/intermediate_layout.png?raw=true)
* [Sophisticated layout](../images/sophisticated_layout.png?raw=true)

To set the network architecture to basic layout, update the type parameter in the LSTM configuration object as follows:

```
config.setType(LSTMConfiguration.TYPE_BASIC_LSTM);
```

Other layouts are LSTMConfiguration.TYPE_2LAYERS_LSTM (intermediate) and LSTMConfiguration.TYPE_3LAYERS_DENSE_LSTM (sophisticated).

### Hyperparameters (or learning parameters)

The most important hyperparameters to be configured and their suggested values are:

* Optimization algorithm (A): Stochastic Gradient Descent (SGD), Adaptive Moment (ADAM) or Adaptive Moment with Nesterov Momentum (NADAM)
* Nodes per hidden layer (H): 16 or 32 nodes
* Training epochs (E): 50, 100 or 200 epochs
* Learning rate (L): 0.01 or 0.005

To set the hyperparameters, update the LSTM configuration object as follows:

```
config.setGradientDescentUpdater(LSTMConfiguration.UPDATER_ADAM);
config.setHiddenLayer1Nodes(16);
config.setNumberOfEpochs(100);
config.setLearningRate(0.005);
```

### Other Configuration Parameters

Parameters that reffer to history files and path, as well as other specific parameters are:

* Instance type (e.g. c5n.2xlarge)
* Availability zone filter (e.g. us-east-1c)
* Training data proportion (e.g. 70% for training and 30% for testing/prediction)
* Number of input values
* Number of output values
* Number of features (usually, features = input + output)
* Input CSV path (for the history files)
* Input CSV file names (for the history files)
* Regularization data (true or false)
* Regularization timestep in hours (e.g. every 1 hour)

To set the parameters, update the LSTM configuration object as follows:

```
config.setInstanceType("c5n.2xlarge");
config.setAvailabilityZoneFilter("us-east-1c");
config.setTrainingDataProportion(0.7);
config.setInputNodes(1);
config.setOutputNodes(1);
config.setNumberOfFeatures(2);
config.setInputCsvPath("src/main/resources/single-analysis/");
config.setInputCsvFileNames(
	new String[] {
		"c5n.2xlarge.2020-04.txt"
	});
config.setRegularizationData(true);
config.setRegularizationTimestepInHours(1);
```

### Running the Customized Configuration

To run the analysis with the customized configuration, import [LSTMBasedAnalysis](./src/main/java/br/unb/cic/laico/analysis/lstm/LSTMBasedAnalysis.java) interface and [LSTMBasedAnalysisBuilder](./src/main/java/br/unb/cic/laico/analysis/lstm/LSTMBasedAnalysisBuilder.java) implementation class into your code:

```
import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysis;
import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysisBuilder;
```

Then create a [LSTMBasedAnalysis](./src/main/java/br/unb/cic/laico/analysis/lstm/LSTMBasedAnalysis.java) object and call the "doAnalysis" method, passing the configuration as parameter, as follows:

```
LSTMBasedAnalysis lstmAnalysis = new LSTMBasedAnalysisBuilder();
lstmAnalysis.doAnalysis(config);
```

The generated gnuplot script file produces the image with training, testing and prediction data, as can be seen [here](../images/A2H16E100L0.005T1-c5n.2xlarge-us-east-1c-gnuplot-script.png?raw=true).

For more other detailed examples, please refer to Section [Building a new Analysis](#building-a-new-analysis) in this documentation page.
