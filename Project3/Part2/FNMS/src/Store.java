// TO DO
// Alter number of buyers arriving to store

import java.util.*;
import java.lang.Math;

class Store extends Publisher implements Utility {
    private Vector<AbstractClerk> clerks_ = new Vector<AbstractClerk>();
    private AbstractClerk activeClerk_;

    public int total_withdrawn_ = 0;
    public CashRegister register_ = new CashRegister();
    public Vector<Item> inventory_ = new Vector<Item>();
    public Vector<Item> sold_ = new Vector<Item>();
    public HashMap<Integer, Vector<Item.ItemType>> orders_ = new HashMap<Integer, Vector<Item.ItemType>>();

    Store() {
        // store start with 3 of each item
        // Making the items is an example of Identity
        // Each individual Item represents a real world object
        for (Item.ItemType itemType : Item.ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        // make decorated clerks with break chances & tuning algorithms
        clerks_.add(new ClerkSellDecorator(new Clerk("Shaggy", 20, new HaphazardTune(), this)));
        clerks_.add(new ClerkSellDecorator(new Clerk("Velma", 5, new ElectronicTune(), this)));
        clerks_.add(new ClerkSellDecorator(new Clerk("Daphne", 10, new ManualTune(), this)));
    }
    
    // override subscribe to also have all workers also be subscribed to
    @Override
    public void Subscribe(Subscriber subscriber) { 
        super.Subscribe(subscriber);
        for (Staff clerk : clerks_) clerk.Subscribe(subscriber);
    } 

    // override ubsubscribe to also have all workers also be unsubscribed from
    @Override
    public void Unsubscribe(Subscriber unsubscriber) { 
        super.Unsubscribe(unsubscriber);
        for (Staff clerk : clerks_) clerk.Unsubscribe(unsubscriber);
    }
    
    // override publish to automatically send name of active clerk
    private void Publish(String context, int data) { super.Publish(context, activeClerk_.GetName(), data); }

    // method for an entire open store day
    public void OpenToday() {
        // choose a clerk, have them check register & go to bank if needed
        // have clerk do inventory and order items if necessary
        // let the store open, clerk handles customers
        // have clerk clean and close the store
        this.ChooseClerk();
        activeClerk_.ArriveAtStore();
        if (!activeClerk_.CheckRegister()) activeClerk_.GoToBank();
        activeClerk_.PlaceOrders(activeClerk_.DoInventory());
        this.Opens();
        activeClerk_.CleanStore();
        activeClerk_.CloseStore();
    }

    // Having the methods in this class private is an example of Encapsulation
    public void ChooseClerk() {
        // pick one of the clerks
        int rando = GetRandomNum(clerks_.size());
        // if the clerk has already worked 3 days in a row, have someone else work
        activeClerk_ = (clerks_.get(rando).GetDaysWorked() < 3) ? clerks_.get(rando) : clerks_.get(GetRandomNumEx(0, clerks_.size(), rando));
// TO DO handle clerk being sick
        // increment days worked for todays clerked
        activeClerk_.IncrementDaysWorked();
        // reset other clerks days worked
        for (Staff clerk : clerks_) {
            if (clerk != activeClerk_) clerk.ResetDaysWorked();
        }
    }

    private Vector<Customer> GenerateCustomers() {
        // make vector to return
        Vector<Customer> toServe = new Vector<Customer>();
        // get random amounts of buyers and sellers in range
        int buyers = GetRandomNum(4, 11);
        int sellers = GetRandomNum(1, 5);
        // create buyers and sellers
        for (int i = 0; i < buyers; i++) { toServe.add(new Buyer()); }
        for (int i = 0; i < sellers; i++) { toServe.add(new Seller()); }
        // shuffle vector so we get customers in random order
        Collections.shuffle(toServe);
        return toServe;
    }

    // store opens for the day
    public void Opens() {
        int itemssold, itemsbought;
        itemssold = itemsbought = 0;
        // make custoemrs and have clerk handle their request
        for (Customer customer : GenerateCustomers()) {
            int result = activeClerk_.HandleCustomer(customer);
            if (result > 0) itemsbought += result; 
            else if (result < 0) itemssold += Math.abs(result);
            customer.LeaveStore();
        }
        // publish number of items sold and bought throughout day
        Publish("itemsold", itemssold);
        Publish("itemsbought", itemsbought);
    }

    // announce that the store is closed
    public void ClosedToday() {
        Print("Today is Day " + Simulation.current_day_ + ", which is Sunday, so the store is closed");
        Publish("closed", 0);
    }

    // show one of the inventories and its value
    // true for remainging items, false for sold items
    public void DisplayInventory(boolean sold) {
        int total = 0;
        Print("Items in " +  (sold ? "sold" : "remaining") + " inventory: ");
        Vector<Item> items = (sold ? sold_ : inventory_);
        for (Item item : items) {
            item.Display();
            total += item.purchase_price_;
        }
        Print("The total value of the " + (sold ? "sold" : "remaining") + " inventory is $" + total);
    }
}