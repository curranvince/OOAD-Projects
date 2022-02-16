//TO DO
// Stop selling clothing (and buying from customers)
// Clerk may be sick
// Decorate sell method (at very bottom)

import java.util.*;
import java.io.*;

class Store implements Utility {
    private int clerk_id_;
    private Vector<Clerk> clerks_ = new Vector<Clerk>();
    protected Vector<Subscriber> subscribers_ = new Vector<Subscriber>();
    
    static int total_withdrawn_ = 0;
    static CashRegister register_ = new CashRegister();
    static Vector<Item> inventory_ = new Vector<Item>();
    static Vector<Item> sold_ = new Vector<Item>();
    
    static HashMap<Integer, Vector<Item.ItemType>> orders_ = new HashMap<Integer, Vector<Item.ItemType>>();

    Store() {
        // store start with 3 of each item
        // Making the items is an example of Identity
        // Each individual Item represents a real world object
        for (Item.ItemType itemType : Item.ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                Store.inventory_.add(ItemFactory.MakeItem(itemType.name()));
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
    
    public void Subscribe(Subscriber subscriber) { 
        subscribers_.add(subscriber); 
        for (Staff clerk : clerks_) clerk.Subscribe(subscriber);
    } 

    public void Unsubscribe(Subscriber unsubscriber) { 
        subscribers_.remove(unsubscriber); 
        for (Staff clerk : clerks_) clerk.Unsubscribe(unsubscriber);
        unsubscriber.Close();
    }

    // methods to handle outputs
    private void Publish(String context, int data) { for (Subscriber subscriber : subscribers_) subscriber.Update(context, GetClerk(), data); }
    
    // methods to get workers
    public Clerk GetClerk() { return clerks_.get(clerk_id_); }

    // Having the methods in this class private is an example of Encapsulation
    public void ChooseClerk() {
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

    public void HandleSunday() {
        // close the store on sundays
        Print("Today is Day " + Simulation.current_day_ + ", which is Sunday, so the store is closed");
        Publish("sunday", 0);
    }

    public void OutputResults() {
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
}