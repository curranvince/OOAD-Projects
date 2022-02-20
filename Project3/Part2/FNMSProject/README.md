IMPORTANT : PROJECT HAS BEEN UPDATED TO USE MAVEN
THIS IS REQUIRED TO BUILD (Makefile can be provided if NECESSARY)

Project runs Java 8 (1.8)

To use the Maven build tools, run these commands from /FNMSProject

Compile:
mvn compile

Test:
mvn test

* Outputs to terminal
* Logs by class can be found in /FNMSProject/target/surefire-reports

Run (please compile first!):
mvn exec:java

* Output will be created in a new directory /FNMSProject/output
* Running again will overwrite previous output

Clean:
mvn clean
