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
* A sample run is provided in that directory already
* Running again will overwrite previous output

Clean:
mvn clean

Assumptions (from project 2):
* We only show a customer one item of the type they're interested in buying. Not each one we have in inventory.
* If we don't have enough money to buy a customers item, broadcast that and let the customer leave.
* When we buy an item from a customer, it gets listed at 2X the purchase price.

New Assumptions or Changes from Writeup:
* The writeup says for every stringed instrument that is sold, we should use a decorator to make sure some other sales are attempted. We did this by decorating the clerks method for handling customers, thus, we made it so only Velma and Daphne try to make the extra sales, but not Shaggy. We did this because we thought it 'showed off' the decorator pattern better. We could easily make it happen for every stringed instrument sale by decorating each clerk (marked in code).
* The writeup asks to make fields like equalized_, tuned_, etc. for multiple different subclases. We found this implementation to be poor due to the reuse of code and bad extensibility. Thus, we introduced a generic 'Tuneable' component which can be attached at any point in the item hierarchy. This reduces duplicated code and elimates the need for casting items when actually tuning them (because we just check if it has the component). 

Contributors:
Vince Curran, Logan Park, Kevin Vo
