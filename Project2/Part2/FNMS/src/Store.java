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

public class Store {
    CashRegister register_ = new CashRegister();
    Vector<Item> inventory_ = new Vector<Item>();
    Vector<Item> sold_ = new Vector<Item>();
    Vector<Clerk> clerks_ = new Vector<Clerk>();
    HashMap<Integer, Vector<ItemType>> orders_ = new HashMap<Integer, Vector<ItemType>>();
    int day_of_week_;
    int current_day_;
    int clerk_id_;
    int total_withdrawn_;

    Store() {
        day_of_week_ = 0;
        total_withdrawn_ = 0;
        current_day_ = 0;

        // store start with 3 of each item
        for (ItemType itemType : ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        
        clerks_.add(new Clerk("Shaggy", 20));
        clerks_.add(new Clerk("Velma", 5));
    }
    
    int GetClerk() {
        int rando = Utility.GetRandomNum(2);
        if (clerks_.get(rando).days_worked_ < 3) {
            return rando;
        } else {
            if (rando == 0) return 1;
            else return 0;
        }
    }

    int GetOffClerk() {
        if (clerk_id_ == 0) return 1;
        else return 0;
    }

    void ArriveAtStore() { 
        System.out.println(clerks_.get(clerk_id_).name_  + " has arrived at the store on Day " + current_day_); 
        if (orders_.containsKey(current_day_)) {
            // need to remove current from map
            for (ItemType type : orders_.get(current_day_)) {
                System.out.println(clerks_.get(clerk_id_).name_  + " finds an order with 3 " + type.name() + "s");
                for (int i = 0; i < 3; i++) {
                    Item toAdd = ItemFactory.MakeItem(type.name());
                    toAdd.day_arrived = current_day_;
                    inventory_.add(toAdd);
                    register_.TakeMoney(inventory_.lastElement().purchase_price_);
                }
            }
            orders_.remove(current_day_);
        } else {
            System.out.println(clerks_.get(clerk_id_).name_  + " finds no orders delivered today"); 
        }
    }

    boolean CheckRegister() {
        System.out.println(clerks_.get(clerk_id_).name_ + " checks the register to find $" + register_.money_ );
        if (register_.money_ >= 75) {
            return true;
        } else {
            return false;
        }
    }

    void GoToBank() {
        System.out.println(clerks_.get(clerk_id_).name_ + " goes to the bank to withdraw $1000 for the register" );
        register_.AddMoney(1000);
        total_withdrawn_ += 1000;
    }

    void DoInventory() {
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
        System.out.println(clerks_.get(clerk_id_).name_ + " does inventory to find we have $" + total + " worth of product");
        
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
                    // if date isnt in order system then add it
                    if (!orders_.containsKey(deliveryDay)) {
                        orders_.put(deliveryDay, new Vector<ItemType>());
                    } 
                    // add order to delivery day
                    orders_.get(deliveryDay).add(type);
                    // broadcast who placed an order of what and what day it will arrive
                    System.out.println(clerks_.get(clerk_id_).name_ + " placed an order for 3 " + type.name() + "s to arrive on Day " + deliveryDay);
                }
            }
        }
    }

    void Sell(Item item, int salePrice) {
        register_.AddMoney(salePrice);
        item.day_sold_ = current_day_;
        item.sale_price_ = salePrice;
        inventory_.remove(item);
        sold_.add(item);
    }

    void Buy(Item item, int salePrice) {
        register_.TakeMoney(salePrice);
        inventory_.add(item);
    }

    void OpenTheStore() {
        Vector<Customer> customers = Utility.MakeCustomers();
        for (Customer customer : customers) {
            customer.DisplayRequest();
            if (customer.buying_) {
                // see if we have an item for them in stock
                boolean found = false;
                for (Item item : inventory_) {
                    if (item.itemType == customer.item_) {
                        found = true;
                        int willBuy = Utility.GetRandomNum(2);
                        System.out.println(clerks_.get(clerk_id_).name_ + " shows the customer the " + item.name_  + ", selling for $" + item.list_price_);
                        if (willBuy == 0) {
                            // customer buys at list price
                            System.out.println("The customer buys the " + item.name_ + " for $" + item.list_price_);
                            Sell(item, item.list_price_);
                            break;
                        } else {
                            // offer discount to get 75% chance of buying
                            System.out.println(clerks_.get(clerk_id_).name_ + " offers a 10% discount");
                            willBuy = Utility.GetRandomNum(4);
                            if (willBuy != 0) {
                                // customer buys at discount
                                int discountPrice = (int)(item.list_price_-(item.list_price_*0.1));
                                System.out.println("The customer buys the " + item.name_ + " for $" + discountPrice);
                                Sell(item, discountPrice);
                                break;
                            } else {
                                System.out.println("The customer decides not to buy the " + item.name_);
                                // no break assumes we want to show the customer each of something we have in stock, not just one
                                // add break here to show customer only one item if they don't buy the first
                                // break;
                            }
                        }
                    }
                }
                if (!found) System.out.println(clerks_.get(clerk_id_).name_ + " informs the customer we have no " + customer.item_.name() + " in stock");
            } else {
                Item item = ItemFactory.MakeItem(customer.item_.name());
                int offerPrice = Utility.GetOfferPrice(item.condition_);
                System.out.println(clerks_.get(clerk_id_).name_ + " determines the " + item.name_ + " to be in " + item.condition_ + " condition and the value to be $" + offerPrice );
                if (register_.HasEnough(offerPrice)) {
                    int willSell = Utility.GetRandomNum(2);
                    if (willSell == 0) {
                        // buy at initial value price
                        System.out.println("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + offerPrice);
                        Buy(item, offerPrice);
                    } else {
                        // add 10% to price and try again
                        // offer discount to get 75% chance of buying
                        System.out.println(clerks_.get(clerk_id_).name_ + " offers a 10% increase to the price");
                        willSell = Utility.GetRandomNum(4);
                        if (willSell != 0) {
                            // customer sells at extra offer price
                            int extraPrice = (int)(offerPrice+(offerPrice*0.1));
                            System.out.println("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + extraPrice);
                            Buy(item, extraPrice);
                        } else {
                            System.out.println("The customer leaves without selling their " + item.name_);
                        }
                    }
                } else {
                    System.out.println("The store doesn't have enough money to buy the " + item.name_);
                }
            }
        }
    }

    void CleanStore() {
        if (!clerks_.get(clerk_id_).Clean()) { 
            // pick a random item for the clerk to break
            int breakIndex = Utility.GetRandomNum(inventory_.size());
            Item toBreak = inventory_.get(breakIndex);
            if (toBreak.condition_ == "Poor") {
                // remove items with poor condition
                System.out.println("Oh no! " + clerks_.get(clerk_id_).name_ + " broke a " + toBreak.name_ + " while cleaning. It has been removed from inventory");
                inventory_.remove(breakIndex);
            } else {
                // lower condition and list price of non-poor quality items
                System.out.println("Oh no! " + clerks_.get(clerk_id_).name_ + " broke a " + toBreak.name_ + " while cleaning");
                toBreak.condition_ = Utility.LowerCondition(toBreak.condition_);
                toBreak.list_price_ -= (int)(toBreak.list_price_*0.2);
                System.out.println("It's condition has worsened to " + inventory_.get(breakIndex).condition_ + ", and its list price has lowered to $" + inventory_.get(breakIndex).list_price_ );
            }
        } else {
            // nothing breaks
            System.out.println(clerks_.get(clerk_id_).name_ + " cleans the store without incident");
        }
    }

    void OutputResults() {
        // display inventory
        System.out.println("Items left in inventory: ");
        int total = 0;
        for (Item item : inventory_) {
            item.Display();
            total += item.purchase_price_;
        }
        System.out.println("The total value of the remaining inventory is $" + total);
        // display items sold
        total =  0;
        System.out.println("Items sold: ");
        for (Item item : sold_) {
            item.DisplaySold();
            total += item.sale_price_;
        }
        System.out.println("The store sold $" + total + " worth of items this month");
        // display money stats
        System.out.println("The store has $" + register_.money_ + " in the register");
        System.out.println("$" + total_withdrawn_ + " was withdrawn from the bank");
    }

    void RunSimulation() {
        // set output stream
        try {
            PrintStream o = new PrintStream(new File("Output.txt"));
            System.setOut(o);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Please create file 'Output.txt' in src folder");
        }
        // each loop represents one day
        for (int i = 0; i < 30; i++) {
            current_day_++;
            // pick whos working
            clerk_id_ = GetClerk();
            // update workers days worked stats
            clerks_.get(GetOffClerk()).ResetDaysWorked();
            clerks_.get(clerk_id_).IncrementDaysWorked();
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
            System.out.println(clerks_.get(clerk_id_).name_ + " locks up and goes home for the night");
        }  
        // display final results
        OutputResults();
    }
}