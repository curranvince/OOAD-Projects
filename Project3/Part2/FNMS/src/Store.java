import java.util.*;
import java.io.*;

enum ItemType {
    PAPERSCORE,
    CD,
    VINYL,
    CDPLAYER,
    RECORDPLAYER,
    MP3PLAYER,
    GUITAR,
    BASS,
    MANDOLIN,
    FLUTE,
    HARMONICA,
    HATS,
    SHIRTS,
    BANDANAS,
    PRACTICEAMPS,
    CABLES,
    STRINGS
}

abstract class Store {
    protected Subscriber[] subscribers_ = new Subscriber[2];
    protected CashRegister register_ = new CashRegister();
    protected Vector<Item> inventory_ = new Vector<Item>();
    protected Vector<Item> sold_ = new Vector<Item>();
    protected Vector<Staff> clerks_ = new Vector<Staff>();
    protected HashMap<Integer, Vector<ItemType>> orders_ = new HashMap<Integer, Vector<ItemType>>();
    protected int current_day_;
    protected int clerk_id_;
    protected int total_withdrawn_;

    Store() {
        total_withdrawn_ = 0;
        current_day_ = 0;
        // create subscribers
        subscribers_[0] = new Tracker();
        // store start with 3 of each item
        // Making the items is an example of Identity
        // Each individual Item represents a real world object
        for (ItemType itemType : ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        // make clerks witht their break chances
        clerks_.add(new Clerk("Shaggy", 20, new ManualTune()));
        clerks_.add(new Clerk("Velma", 5, new HaphazardTune()));
        clerks_.add(new Clerk("Daphne", 10, new ElectronicTune()));
    }
    
    // methods to handle outputs
    private void Publish(String context, int data) { for (Subscriber subscriber : subscribers_) subscriber.Update(context, GetClerk(), data); }
    private void Print(String str) { System.out.println(str); }
    
    // methods to get workers
    private Staff GetClerk() { return clerks_.get(clerk_id_); }

    // Having the methods in this class private is an example of Encapsulation
    private void ChooseClerk() {
        // pick one of the two clerks
        int rando = Utility.GetRandomNum(clerks_.size());
        // if the clerk has already worked 3 days in a row, have someone else work
        clerk_id_ = (clerks_.get(rando).GetDaysWorked() < 3) ? rando : Utility.GetRandomNumEx(0, clerks_.size(), rando);
        // increment days worked for todays clerked
        GetClerk().IncrementDaysWorked();
        // reset other clerks days worked
        for (int i = 0; i < clerks_.size(); i++) {
            if (i != clerk_id_) clerks_.get(i).ResetDaysWorked();
        }
    }

    private void ArriveAtStore() { 
        Print(GetClerk().name_  + " has arrived at the store on Day " + current_day_); 
        // check if theres any orders for today
        if (orders_.containsKey(current_day_)) {
            // receive orders for each type
            for (ItemType type : orders_.get(current_day_)) {
                Print(GetClerk().name_  + " finds an order with 3 " + type.name() + "s");
                for (int i = 0; i < 3; i++) {
                    Item toAdd = ItemFactory.MakeItem(type.name());
                    toAdd.day_arrived = current_day_;
                    inventory_.add(toAdd);
                    register_.TakeMoney(inventory_.lastElement().purchase_price_);
                }
            }
            // remove orders from the map
            orders_.remove(current_day_);
        } else {
            // if no orders today then broadcast it
            Print(GetClerk().name_  + " finds no orders delivered today"); 
        }
    }

    private boolean CheckRegister() {
        // broadcast register amount and return if its greater than 75 or not
        Print(GetClerk().name_ + " checks the register to find $" + register_.GetAmount());
        return (register_.GetAmount() >= 75) ? true : false;
    }

    private void GoToBank() {
        // add 1000 to register and broadcast
        Print(GetClerk().name_ + " goes to the bank to withdraw $1000 for the register" );
        register_.AddMoney(1000);
        total_withdrawn_ += 1000;
    }

    private void DoInventory() {
        // vector to keep track of all types
        Set<ItemType> allItemTypes = new HashSet<ItemType>();
        Collections.addAll(allItemTypes, ItemType.values()); // https://www.geeksforgeeks.org/java-program-to-convert-array-to-vector/
        Set<ItemType> foundTypes = new HashSet<ItemType>(); 
        int total = 0;
        for (Item item : inventory_) {
            // for every type we have remove it so were left with only types we dont have in stock
            if (!foundTypes.contains(item.itemType)) foundTypes.add(item.itemType);
            // add value of item to total
            total += item.purchase_price_;
        }
        // broadcast total value of inventory
        Print(clerks_.get(clerk_id_).name_ + " does inventory to find we have $" + total + " worth of product");
        
        // find missing items through difference of the all set and the found set
        allItemTypes.removeAll(foundTypes);
        if (!allItemTypes.isEmpty()) {
            // remove items weve already ordered
            for (Vector<ItemType> vec : orders_.values()) {
                for (ItemType orderedItem : vec) {
                    allItemTypes.remove(orderedItem);
                }
                if (allItemTypes.isEmpty()) break;
            }
            // place orders
            if (!allItemTypes.isEmpty()) {
                for (ItemType type : allItemTypes) {
                    int deliveryDay = Utility.GetRandomNum(1, 4) + current_day_;
                    // make sure orders arent delivered on Sunday
                    if (deliveryDay % 7 == 0) { deliveryDay++;}
                    // if date isnt in order system then add it
                    if (!orders_.containsKey(deliveryDay)) {
                        orders_.put(deliveryDay, new Vector<ItemType>());
                    } 
                    // add order to delivery day
                    orders_.get(deliveryDay).add(type);
                    // broadcast who placed an order of what and what day it will arrive
                    Print(GetClerk().name_ + " placed an order for 3 " + type.name() + "s to arrive on Day " + deliveryDay);
                }
            } else {
                Print(GetClerk().name_ + " places no orders today");
            }
        } else {
            Print(GetClerk().name_ + " places no orders today");
        }
    }

    // sell an item to a customer
    protected void Sell(Item item, int salePrice) {
        // add money to register
        register_.AddMoney(salePrice);
        // update item sale price and day
        item.day_sold_ = current_day_;
        item.sale_price_ = salePrice;
        // remove item from inventory and add to sold collection
        inventory_.remove(item);
        sold_.add(item);
    }

    // buy an item from a customer
    private void Buy(Item item, int salePrice) {
        // take money from register and
        register_.TakeMoney(salePrice);
        item.purchase_price_ = salePrice;
        item.list_price_ = salePrice*2;
        item.day_arrived = current_day_;
        inventory_.add(item);
    }

    //run a day at the store
    private void OpenTheStore() {
        // generate customers for the day
        Vector<Customer> customers = Utility.MakeCustomers();
        for (Customer customer : customers) {
            // see if the customer wants to buy or sell
            customer.DisplayRequest();
            if (customer.buying_) {
                // see if we have an item for them in stock
                boolean found = false;
                for (Item item : inventory_) {
                    if (item.itemType == customer.item_) {
                        // if we have item in stock, see if theyll buy it (50% chance)
                        found = true;
                        int willBuy = Utility.GetRandomNum(2);
                        Print(GetClerk().name_ + " shows the customer the " + item.name_  + ", selling for $" + item.list_price_);
                        if (willBuy == 0) {
                            // sell item to customer at list price
                            Print("The customer buys the " + item.name_ + " for $" + item.list_price_);
                            Sell(item, item.list_price_);
                            break;
                        } else {
                            // if they dont buy, offer discount to get 75% chance of buying
                            Print(GetClerk().name_ + " offers a 10% discount");
                            willBuy = Utility.GetRandomNum(4);
                            if (willBuy != 0) {
                                // sell item to customer at discounted price
                                int discountPrice = (int)(item.list_price_-(item.list_price_*0.1));
                                Print("The customer buys the " + item.name_ + " for $" + discountPrice);
                                Sell(item, discountPrice);
                                break;
                            } else {
                                Print("The customer decides not to buy the " + item.name_);
                                // no break assumes we want to show the customer each of something we have in stock, not just one
                                // add break here to show customer only one item if they don't buy the first
                                // break;
                            }
                        }
                    }
                }
                // if no item in stock tell the customer we have none
                if (!found) Print(GetClerk().name_ + " informs the customer we have no " + customer.item_.name() + " in stock");
            } else {
                // evaluate the customers item
                Item item = ItemFactory.MakeItem(customer.item_.name());
                int offerPrice = Utility.GetOfferPrice(item.condition_);
                Print(GetClerk().name_ + " determines the " + item.name_ + " to be in " + item.condition_ + " condition and the value to be $" + offerPrice );
                // if we have enough $, offer to buy the item
                if (register_.HasEnough(offerPrice)) {
                    int willSell = Utility.GetRandomNum(2);
                    if (willSell == 0) {
                        // buy item at initial offer price
                        Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + offerPrice);
                        Buy(item, offerPrice);
                    } else {
                        // if customer disagrees, offer 10% increase to price and try again
                        Print(GetClerk().name_ + " offers a 10% increase to the price");
                        willSell = Utility.GetRandomNum(4);
                        if (willSell != 0) {
                            // store buys item at 10% extra price
                            int extraPrice = (int)(offerPrice+(offerPrice*0.1));
                            Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + extraPrice);
                            Buy(item, extraPrice);
                        } else {
                            // if the customer disagrees again let them leave
                            Print("The customer leaves without selling their " + item.name_);
                        }
                    }
                } else {
                    // if we dont have enough money broadcast that
                    Print("The store doesn't have enough money to buy the " + item.name_);
                }
            }
        }
    }

    private void CleanStore() {
        Print("The store closes for the day and " + GetClerk().name_ + " begins cleaning");
        if (!GetClerk().Clean()) { 
            // pick a random item for the clerk to break
            int breakIndex = Utility.GetRandomNum(inventory_.size());
            Item toBreak = inventory_.get(breakIndex);
            if (!toBreak.LowerCondition()) {
                // remove items with poor condition
                Print("Oh no! " + GetClerk().name_ + " broke a " + toBreak.name_ + " while cleaning. It has been removed from inventory");
                inventory_.remove(breakIndex);
            } else {
                // lower condition and list price of non-poor quality items
                Print("Oh no! " + GetClerk().name_ + " broke a " + toBreak.name_ + " while cleaning");
                Print("It's condition has worsened to " + inventory_.get(breakIndex).condition_ + ", and its list price has lowered to $" + inventory_.get(breakIndex).list_price_ );
            }
        } else {
            // nothing breaks
            Print(GetClerk().name_ + " cleans the store without incident");
        }
    }

    private void OutputResults() {
        // display inventory & its value
        Print("Items left in inventory: ");
        int total = 0;
        for (Item item : inventory_) {
            item.Display();
            total += item.purchase_price_;
        }
        Print("The total value of the remaining inventory is $" + total);
        // display items sold & their value
        total =  0;
        Print("Items sold: ");
        for (Item item : sold_) {
            item.DisplaySold();
            total += item.sale_price_;
        }
        Print("The store sold $" + total + " worth of items this month");
        // display money stats
        Print("The store has $" + register_.GetAmount() + " in the register");
        Print("$" + total_withdrawn_ + " was withdrawn from the bank");
    }

    void RunSimulation() {
        // set output stream
        try {
            File file = new File("Output.txt");
            file.createNewFile();
            System.setOut(new PrintStream(file));
        } catch (IOException e) {
            Print("Error: Store could not set output to Output.txt");
            e.printStackTrace();
        }
        // each loop represents one day
        for (int i = 0; i < 30; i++) {
            current_day_++;
            subscribers_[1] = new Logger(current_day_);
            if (current_day_ % 7 != 0) {
                // pick whos working
                ChooseClerk();
                // accept deliveries
                ArriveAtStore();
                // check the register & go to bank if we're broke
                if (!CheckRegister()) { GoToBank(); }
                // do inventory and order items
                DoInventory();
                // open store
                OpenTheStore();
                // clean the store
                CleanStore();
                // announce the end of the day
                Print(GetClerk().name_ + " locks up and goes home for the night");
            } else {
                // close the store on sundays
                Print("Today is Day " + current_day_ + ", which is Sunday, so the store is closed.");
            }
        }  
        // display final results
        OutputResults();
    }
}

// This is an example of a Decorator
class StoreDecorator extends Store {
    StoreDecorator() { super(); }
    // decorated version of sell an item to a customer
    protected void Sell(Item item, int salePrice) {
        //Print("Decorating sell method");
        // add money to register
        register_.AddMoney(salePrice);
        // update item sale price and day
        item.day_sold_ = current_day_;
        item.sale_price_ = salePrice;
        // remove item from inventory and add to sold collection
        inventory_.remove(item);
        sold_.add(item);
    }
}