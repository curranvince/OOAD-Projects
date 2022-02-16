//TO DO
// Stop selling clothing (and buying from customers)
// Clerk may be sick
// Decorate sell method (at very bottom)

import java.util.*;
import java.io.*;

abstract class Store implements Utility {
    protected Subscriber[] subscribers_ = new Subscriber[2];
    protected CashRegister register_ = new CashRegister();
    protected Vector<Item> inventory_ = new Vector<Item>();
    protected Vector<Item> sold_ = new Vector<Item>();
    protected Vector<Staff> clerks_ = new Vector<Staff>();
    protected HashMap<Integer, Vector<Item.ItemType>> orders_ = new HashMap<Integer, Vector<Item.ItemType>>();
    protected int current_day_;
    protected int clerk_id_;
    protected int total_withdrawn_;

    Store() {
        total_withdrawn_ = 0;
        current_day_ = 0;
        // store start with 3 of each item
        // Making the items is an example of Identity
        // Each individual Item represents a real world object
        for (Item.ItemType itemType : Item.ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        // make clerks witht their break chances
        clerks_.add(new Clerk("Shaggy", 20, new HaphazardTune()));
        clerks_.add(new Clerk("Velma", 5, new ElectronicTune()));
        clerks_.add(new Clerk("Daphne", 10, new ManualTune()));
        // set output stream
        try {
            File file = new File("Output.txt");
            file.createNewFile();
            System.setOut(new PrintStream(file));
        } catch (IOException e) {
            Print("Error: Store could not set output to Output.txt");
            e.printStackTrace();
        }
    }
    
    // methods to handle outputs
    private void Publish(String context, int data) { for (Subscriber subscriber : subscribers_) subscriber.Update(context, GetClerk(), data); }
    
    // methods to get workers
    private Staff GetClerk() { return clerks_.get(clerk_id_); }

    // Having the methods in this class private is an example of Encapsulation
    private void ChooseClerk() {
        // pick one of the clerks
        int rando = GetRandomNum(clerks_.size());
        // if the clerk has already worked 3 days in a row, have someone else work
        clerk_id_ = (clerks_.get(rando).GetDaysWorked() < 3) ? rando : GetRandomNumEx(0, clerks_.size(), rando);
// TO DO handle clerk being sick
        // increment days worked for todays clerked
        GetClerk().IncrementDaysWorked();
        // reset other clerks days worked
        for (int i = 0; i < clerks_.size(); i++) {
            if (i != clerk_id_) clerks_.get(i).ResetDaysWorked();
        }
    }

    private void ArriveAtStore() { 
        Publish("arrival", 0);
        Print(GetClerk().name_  + " has arrived at the store on Day " + current_day_); 
        int orders_received = 0;
        // check if theres any orders for today
        if (orders_.containsKey(current_day_)) {
            // receive orders for each type
            for (Item.ItemType type : orders_.get(current_day_)) {
                Print(GetClerk().name_  + " finds an order with 3 " + type.name() + "s");
                for (int i = 0; i < 3; i++) {
                    Item toAdd = ItemFactory.MakeItem(type.name());
                    toAdd.day_arrived = current_day_;
                    inventory_.add(toAdd);
                    register_.TakeMoney(inventory_.lastElement().purchase_price_);
                    orders_received++;
                }
            }
            // remove orders from the map
            orders_.remove(current_day_);
        } else {
            // if no orders today then broadcast it
            Print(GetClerk().name_  + " finds no orders delivered today"); 
        }
        Publish("itemsadded", orders_received);
    }

    private boolean CheckRegister() {
        Publish("checkedregister", register_.GetAmount());
        // broadcast register amount and return if its greater than 75 or not
        Print(GetClerk().name_ + " checks the register to find $" + register_.GetAmount());
        return (register_.GetAmount() >= 75) ? true : false;
    }

    private void GoToBank() {
        // add 1000 to register and broadcast
        Publish("checkedregister", register_.GetAmount());
        Print(GetClerk().name_ + " goes to the bank to withdraw $1000 for the register" );
        register_.AddMoney(1000);
        total_withdrawn_ += 1000;
    }

    private void DoInventory() {
        // vector to keep track of what types we need to order (start with all and remove)
        Set<Item.ItemType> orderTypes = new HashSet<Item.ItemType>();
        Collections.addAll(orderTypes, Item.ItemType.values()); // https://www.geeksforgeeks.org/java-program-to-convert-array-to-vector/
        int total, totalitems, damaged, orders;
        total = totalitems = damaged = orders = 0;
        Iterator<Item> it = inventory_.iterator(); // https://www.w3schools.com/java/java_iterator.asp#:~:text=An%20Iterator%20is%20an%20object,util%20package.
        while (it.hasNext()) {
            Item item = it.next();
            // remove this type from the list of oens we need to order
            if (orderTypes.contains(item.itemType)) orderTypes.remove(item.itemType);
            // tune certain items
            if (item.NeedsTuning()) {
                if (!GetClerk().Tune(item)) {
                    if (item.condition_ == "Broken") it.remove();
                    damaged++;
                }
            }
            // add value of item to total
            total += item.purchase_price_;
            totalitems++;
        }
        // broadcast total value of inventory
        Publish("brokeintuning", damaged);
        Print(GetClerk().name_ + " does inventory to find we have $" + total + " worth of product");
        Publish("totalitems", totalitems);
        Publish("totalitemsprice", total);
        
        if (!orderTypes.isEmpty()) {
            // remove items weve already ordered
            for (Vector<Item.ItemType> vec : orders_.values()) {
                for (Item.ItemType orderedItem : vec) { orderTypes.remove(orderedItem); }
                if (orderTypes.isEmpty()) break;
            }
            // place orders
            if (!orderTypes.isEmpty()) {
                orders = PlaceOrders(orderTypes);
            } else {
                Print(GetClerk().name_ + " places no orders today");
            }
        } else {
            Print(GetClerk().name_ + " places no orders today");
        }
        Publish("itemsordered", orders);
    }

    private int PlaceOrders(Set<Item.ItemType> orderTypes) {
        int orders = 0;
        for (Item.ItemType type : orderTypes) {
//TO DO 
// stop ordering items we no longer sell
            int deliveryDay = GetRandomNum(1, 4) + current_day_;
            // make sure orders arent delivered on Sunday
            if (deliveryDay % 7 == 0) { deliveryDay++; }
            // if date isnt in order system then add it
            if (!orders_.containsKey(deliveryDay)) { orders_.put(deliveryDay, new Vector<Item.ItemType>()); } 
            // add order to delivery day
            orders_.get(deliveryDay).add(type);
            // broadcast who placed an order of what and what day it will arrive
            Print(GetClerk().name_ + " placed an order for 3 " + type.name() + "s to arrive on Day " + deliveryDay);
            orders += 3;
        }
        return orders;
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
        int itemssold = 0;
        int itemsbought = 0;
        Vector<Customer> customers = MakeCustomers();
        for (Customer customer : customers) {
            // see if the customer wants to buy or sell
            customer.DisplayRequest();
            if (customer.IsBuying()) {
                // see if we have an item for them in stock
                boolean found = false;
                for (Item item : inventory_) {
                    if (item.itemType == customer.GetItemType()) {
                        // if we have item in stock, see if theyll buy it (50% chance)
                        found = true;
                        int willBuy = GetRandomNum(2);
                        Print(GetClerk().name_ + " shows the customer the " + item.name_  + ", selling for $" + item.list_price_);
                        if (willBuy == 0) {
                            // sell item to customer at list price
                            Print("The customer buys the " + item.name_ + " for $" + item.list_price_);
                            Sell(item, item.list_price_);
                            itemssold++;
                            break;
                        } else {
                            // if they dont buy, offer discount to get 75% chance of buying
                            Print(GetClerk().name_ + " offers a 10% discount");
                            willBuy = GetRandomNum(4);
                            if (willBuy != 0) {
                                // sell item to customer at discounted price
                                int discountPrice = (int)(item.list_price_-(item.list_price_*0.1));
                                Print("The customer buys the " + item.name_ + " for $" + discountPrice);
                                Sell(item, discountPrice);
                                itemssold++;
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
                if (!found) Print(GetClerk().name_ + " informs the customer we have no " + customer.GetItemType().name() + " in stock");
            } else {
                // evaluate the customers item
                Item item = ItemFactory.MakeItem(customer.GetItemType().name());
// TO DO
// Check if we still sell the item, if we dont: tell customer
// we dont want the item and do not buy it from them
                int offerPrice = GetOfferPrice(item.condition_);
                Print(GetClerk().name_ + " determines the " + item.name_ + " to be in " + item.condition_ + " condition and the value to be $" + offerPrice );
                // if we have enough $, offer to buy the item
                if (register_.HasEnough(offerPrice)) {
                    int willSell = GetRandomNum(2);
                    if (willSell == 0) {
                        // buy item at initial offer price
                        Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + offerPrice);
                        Buy(item, offerPrice);
                        itemsbought++;
                    } else {
                        // if customer disagrees, offer 10% increase to price and try again
                        Print(GetClerk().name_ + " offers a 10% increase to the price");
                        willSell = GetRandomNum(4);
                        if (willSell != 0) {
                            // store buys item at 10% extra price
                            int extraPrice = (int)(offerPrice+(offerPrice*0.1));
                            Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + extraPrice);
                            Buy(item, extraPrice);
                            itemsbought++;
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
        Publish("itemsold", itemssold);
        Publish("itemsbought", itemsbought);
    }

    private void CleanStore() {
        if (!GetClerk().Clean()) { 
            // pick a random item for the clerk to break
            int breakIndex = GetRandomNum(inventory_.size());
            Item toBreak = inventory_.get(breakIndex);
            Print("Oh no! " + GetClerk().name_ + " broke a " + toBreak.name_ + " while cleaning");
            if (!toBreak.LowerCondition()) {
                // remove items with poor condition
                Print("It was already in poor condition, so it has been removed from inventory");
                inventory_.remove(breakIndex);
            } else {
                // lower condition and list price of non-poor quality items
                Print("It's condition has worsened to " + toBreak.condition_ + ", and its list price has lowered to $" + toBreak.list_price_ );
            }
            Publish("damagedcleaning", 1);
        } else {
            // nothing breaks
            Print(GetClerk().name_ + " cleans the store without incident");
            Publish("damagedcleaning", 0);
        }
    }

    // every night, broadcast who left, show the trackers data, and close the logger
    private void CloseStore() {
        Print(GetClerk().name_ + " locks up and goes home for the night");
        Publish("leftstore", 0);
        subscribers_[0].ShowData(current_day_);
        subscribers_[1].Close();
    }

    private void HandleSunday() {
        // close the store on sundays
        Print("Today is Day " + current_day_ + ", which is Sunday, so the store is closed");
        Publish("sunday", 0);
        subscribers_[1].Close();
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

    void RunSimulation(int n) {
        Print(" *** BEGINNING SIMULATION *** \n");
        // create Tracker
        subscribers_[0] = new Tracker();
        // each loop represents one day
        for (int i = 0; i < n; i++) {
            // iterate day and create daily logger
            current_day_++;
            Print(" ***SIMULATION : DAY " + current_day_ + " BEGINNING***");
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
                // run the store day
                OpenTheStore();
                // clean the store
                CleanStore();
                // end the day
                CloseStore();
            } else {
                // close the store on sundays
                HandleSunday();
            }
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        // display final results
        OutputResults();
        Print("\n *** SIMULATION COMPLETE *** ");
    }
}

// This is an example of the Decorator pattern
class StoreDecorator extends Store {
    StoreDecorator() { super(); }
    // decorated version of sell an item to a customer
    protected void Sell(Item item, int salePrice) {
        super.Sell(item, salePrice); // https://docs.oracle.com/javase/tutorial/java/IandI/super.html
// TO DO : finish decorating method

    }
}