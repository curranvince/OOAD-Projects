IMPORTANT : PROJECT HAS BEEN UPDATED TO USE MAVEN
THIS IS REQUIRED TO BUILD (Makefile can be provided if NECESSARY)

Project runs Java 11

To use the Maven build tools, run these commands from /FNMSProject

Compile:
mvn compile

Test:
mvn test

* Outputs to terminal
* Logs by class can be found in /FNMSProject/target/surefire-reports
* Note specific test information is only output when tests fail, other than that just says how many pass
*   This means to see whats being tested look at * Test files in src/test

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

Assumptions (from project 3):
* The writeup says for every stringed instrument that is sold, we should use a decorator to make sure some other sales are attempted. We did this by decorating the clerks method for handling customers, thus, we made it so 3 clerks make the extra sales, and 3 do not (they are lazy salespeople). We did this because we thought it 'showed off' the decorator pattern better. We could easily make it happen for every stringed instrument sale by decorating each clerk (marked in code).
* The writeup asks to make fields like equalized_, tuned_, etc. for multiple different subclases. We found this implementation to be poor due to the reuse of code and bad extensibility. Thus, we introduced a generic 'Tuneable' component which can be attached at any point in the item hierarchy. This reduces duplicated code and elimates the need for casting items when actually tuning them (because we just check if it has the component). 
* Our interpretation/implementation of the 'discontinue' feature: When a Clothing subclass runs out, it will no longer be ordered, but can still be bought from customers. When all clothing subclasses are discontinued, they are no longer bought from customers.

New Assumptions / Changes:
* The user starts with no items in their inventory. If they want to sell an item when they have none, they will be allowed to choose an itemtype to be generated for them to sell. If a user has items in their inventory, they will choose from these items when attempting to sell to a store. 
* When a user wants to buy an item, they can choose any itemtype. The store will respond appropiately about the item being available, discontinued, etc. 
* The graphs for register money, item sales, items broken, etc. are aggregated between the 2 stores for each day. IE if Store 1 has $1000 and Store 2 has $1000 at the end of Day 1, the graph would show $2000. These are MoneyGraph and ItemGraph.
* New graphs have been added in addition to the above. The ComparisonGraph shows the register money and daily sales for each store individually. The AveragesGraph shows average statistics per day for each clerk, as calculated at end of simulation from their actual values.
* Statistics show values taken from the end of the day. Or more specifically, they keep track of each event throughout the day, then add them up and write them at the end of the day.


Contributors:
Vince Curran, Logan Park, Kevin Vo
