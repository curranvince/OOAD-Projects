import java.util.Collections;
import java.util.Vector;

import java.util.*;

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

    void OpenStore() { 
        System.out.println(clerks_.get(clerk_id_).name_  + " has arrived at the store on Day " + current_day_); 
        if (orders_.containsKey(current_day_)) {
            for (ItemType type : orders_.get(current_day_)) {
                System.out.println(clerks_.get(clerk_id_).name_  + " finds an order with 3 " + type.name() + "s");
                for (int i = 0; i < 3; i++) {
                    inventory_.add(ItemFactory.MakeItem(type.name()));
                }
            }
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
        if (allItemTypes.size() > 0) {
            // remove items weve already ordered
            for (ItemType type : allItemTypes) {
                for (Vector<ItemType> vec : orders_.values()) {
                    for (ItemType orderedItem : vec) {
                        if (type == orderedItem) allItemTypes.remove(orderedItem);
                    }
                }
            }
            // place orders
            if (allItemTypes.size() > 0) {
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

    void RunSimulation() {
        // display inventory
        
        // each day goes in here
        for (int i = 0; i < 8; i++) {
            current_day_++;
            // pick whos working
            clerk_id_ = GetClerk();
            // update workers days worked stats
            clerks_.get(GetOffClerk()).ResetDaysWorked();
            clerks_.get(clerk_id_).IncrementDaysWorked();
            // open store, accept deliveries
            OpenStore();
            // displays inventory
            for (Item item : inventory_) {
                item.Display();
            }
            // check the register
            if (!CheckRegister()) {
                // if not enough in register go to bank
                GoToBank();
            }
            // do inventory and order items
            DoInventory();
            
            if (current_day_ == 3) {
                inventory_.removeElementAt(0);
                inventory_.removeElementAt(0);
                inventory_.removeElementAt(0);
            }
        }  
        
    };
}
