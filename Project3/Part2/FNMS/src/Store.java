import java.util.*;
import java.io.*;

class Store extends Publisher implements Utility {
    private int clerk_id_;
    private Vector<Staff> clerks_ = new Vector<Staff>();
    private Vector<Subscriber> subscribers_ = new Vector<Subscriber>();
    private Staff activeClerk_;

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
        //clerks_.add(new Clerk("Shaggy", 20, new HaphazardTune()));
        clerks_.add(new StaffSellDecorator(new Clerk("Shaggy", 20, new HaphazardTune(), this)));
        clerks_.add(new StaffSellDecorator(new Clerk("Velma", 5, new ElectronicTune(), this)));
        clerks_.add(new StaffSellDecorator(new Clerk("Daphne", 10, new ManualTune(), this)));
    }
    
    @Override
    public void Subscribe(Subscriber subscriber) { 
        super.Subscribe(subscriber);
        for (Staff clerk : clerks_) clerk.Subscribe(subscriber);
    } 

    @Override
    public void Unsubscribe(Subscriber unsubscriber) { 
        super.Unsubscribe(unsubscriber);
        for (Staff clerk : clerks_) clerk.Unsubscribe(unsubscriber);
    }
    
    protected void Publish(String context, int data) { super.Publish(context, activeClerk_.GetName(), data); }

    // methods to get workers
    //public Staff GetClerk() { return activeClerk_; }

    public void OpenToday() {
        ChooseClerk();
        activeClerk_.ArriveAtStore();
        if (!activeClerk_.CheckRegister()) activeClerk_.GoToBank();
        activeClerk_.PlaceOrders(activeClerk_.DoInventory());
        HaveCustomers();
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
        for (int i = 0; i < buyers; i++) {
            toServe.add(new Customer(true));
        }
        for (int i = 0; i < sellers; i++) {
            toServe.add(new Customer(false));
        }
        // shuffle vector so we get customers in random order
        Collections.shuffle(toServe);
        return toServe;
    }

    public void HaveCustomers() {
        // generate customers for the day
        int itemssold = 0;
        int itemsbought = 0;
        for (Customer customer : GenerateCustomers()) {
            // see if the customer wants to buy or sell
            customer.DisplayRequest();
            if (customer.IsBuying()) {
                // have clerk check for item in stock
                if (activeClerk_.CheckForItem(customer.GetItemType()) != null) {
                    // if we do, have clerk try to sell item
                    if (activeClerk_.TryToSell(activeClerk_.CheckForItem(customer.GetItemType()))) itemssold++;
                } else {
                    Print(activeClerk_.GetName()+ " informs the customer we have no " + customer.GetItemType().name() + " in stock");
                }
            } else {
                // clerk tries to buy customers item
                if (activeClerk_.TryToBuy(ItemFactory.MakeItem(customer.GetItemType().name()))) itemsbought++;
            }
        }
        Publish("itemsold", itemssold);
        Publish("itemsbought", itemsbought);
    }

    public void ClosedToday() {
        // handle the store being closed
        Print("Today is Day " + Simulation.current_day_ + ", which is Sunday, so the store is closed");
        Publish("closed", 0);
    }

    public void DisplayInventory(boolean sold) {
        int total = 0;
        Print("Items in " +  (sold ? "sold" : "remaining") + " inventory: ");
        Vector<Item> items = (sold ? sold_ : inventory_);
        for (Item item : items) {
            item.Display();
            total += item.purchase_price_;
        }
        Print("The total value of the " + (sold ? "sold" : "remaining") + "  inventory is $" + total);
    }
}