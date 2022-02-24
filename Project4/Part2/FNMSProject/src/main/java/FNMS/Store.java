package FNMS;

import java.util.*;

import FNMS.Customer.RequestType;
import FNMS.Item.ItemType;

// Publishers have a list of subscribers which can be subscribed/unsubscribed to
// They can also publish information to their subscribers
abstract class Publisher implements Utility {
    private List<Subscriber> subscribers_ = new ArrayList<Subscriber>();

    public void Subscribe(Subscriber subscriber) { subscribers_.add(subscriber); } 
    public void Unsubscribe(Subscriber unsubscriber) { subscribers_.remove(unsubscriber); }
    
    protected void Publish(String context, String name, int data) { for (Subscriber subscriber : subscribers_) subscriber.Update(context, name, data); }
}

class Store extends Publisher {
    // CashRegister class to handle the Stores $
    // Good example of Cohesion because the class has
    // one specifc purpose (handling money/doing simple math)
    public class CashRegister {
        private int money_;

        CashRegister() { money_ = 0; }
        
        int GetAmount() { return money_; }
        
        void AddMoney(int money) { money_ += money; }

        boolean TakeMoney(int money) { 
            if ((money_ - money) >= 0) {
                money_ -= money; 
                return true;
            }
            return false;
        }
    }

    private List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    private AbstractClerk activeClerk_;

    public String name_;
    public int total_withdrawn_ = 0;
    public KitFactory kitFactory_;
    public CashRegister register_ = new CashRegister();
    public List<Item> inventory_ = new ArrayList<Item>();
    public List<Item> sold_ = new ArrayList<Item>();
    public List<ItemType> discontinued_ = new ArrayList<ItemType>();
    public HashMap<Integer, List<ItemType>> orders_ = new HashMap<Integer, List<ItemType>>();
    
    Store(String name, KitFactory kitFactory) {
        kitFactory_ = kitFactory;
        name_ = name;
        // store start with 3 of each item
        // Making the items is an example of Identity
        // Each individual Item represents a real world object
        for (ItemType itemType : ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
    }
    
    public void Discontiue(ItemType itemType) { 
        Print("The store has officially discontinued " + itemType + ", so it will no longer order them");
        discontinued_.add(itemType); 
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
        activeClerk_.ArriveAtStore();
        if (!activeClerk_.CheckRegister()) activeClerk_.GoToBank();
        activeClerk_.PlaceOrders(activeClerk_.DoInventory());
        this.Opens();
        activeClerk_.CleanStore();
        activeClerk_.CloseStore();
    }

    public void UpdateClerk(AbstractClerk clerk) { 
        activeClerk_ = clerk; 
        activeClerk_.UpdateStore(this);
    }

    private List<Customer> GenerateCustomers() {
        // make vector to return
        List<Customer> toServe = new ArrayList<Customer>();
        // get random amounts of buyers and sellers in range
        int buyers = 2 + GetPoissonRandom(3);
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
            Pair<RequestType, Integer> results = activeClerk_.HandleCustomer(customer);
            switch (results.getKey()) {
                case Buy:
                    itemssold += results.getValue();
                    break;
                case Sell:
                    itemsbought += results.getValue();
                    break;
                default:
                    break;
            }
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
        List<Item> items = (sold ? sold_ : inventory_);
        for (Item item : items) {
            item.Display();
            total += item.purchase_price_;
        }
        Print("The total value of the " + (sold ? "sold" : "remaining") + " inventory is $" + total + "\n");
    }
}