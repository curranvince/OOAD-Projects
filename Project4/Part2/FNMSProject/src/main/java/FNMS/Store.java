package FNMS;

import java.util.*;

import FNMS.Item.ItemType;

// Publishers have a list of subscribers which can be subscribed/unsubscribed to
// They can also publish information to their subscribers
abstract class Publisher implements Utility {
    protected List<Subscriber> subscribers_ = new ArrayList<Subscriber>();
    protected LinkedList<Customer> customers_ = new LinkedList<Customer>();

    public void Subscribe(Subscriber subscriber) { subscribers_.add(subscriber); } 
    public void Unsubscribe(Subscriber unsubscriber) { subscribers_.remove(unsubscriber); }
    public void UnsubscribeAll() { subscribers_ = new ArrayList<Subscriber>(); }
    
    protected void Publish(MyEvent event) { for (Subscriber subscriber : subscribers_) subscriber.Update(event); }
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

    private String name_;
    private int total_withdrawn_ = 0;
    private AbstractClerk activeClerk_;
   
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

    public String getName() { return name_; }
    public void updateWithdrawn(int withdrawn) { total_withdrawn_ += withdrawn; }
    public int getWithdrawn() { return total_withdrawn_; }

    public AbstractClerk GetActiveClerk() { return activeClerk_; }
    public void UpdateClerk(AbstractClerk clerk) { 
        activeClerk_ = clerk; 
        activeClerk_.UpdateStore(this);
    }

    public void QueueCustomers(List<Customer> customers) { customers_.addAll(customers); }

    public void Discontiue(ItemType itemType) { 
        Print("The store has officially discontinued " + itemType + ", so it will no longer order them");
        discontinued_.add(itemType); 
    }

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

    // store opens for the day
    public void Opens() {
        int itemssold, itemsbought;
        itemssold = itemsbought = 0;
        while (!customers_.isEmpty()) { // https://stackoverflow.com/questions/57715470/iterating-a-list-until-the-list-is-empty
            Iterator<Customer> it = customers_.listIterator();
            while (it.hasNext()) {
                Customer customer = it.next();
                customer.MakeRequest();
                customer.LeaveStore();
                it.remove();
            }
        }
    }

    // announce that the store is closed
    public void ClosedToday() {
        Print("Today is Day " + Simulation.current_day_ + ", which is Sunday, so the store is closed");
        Publish(new ClosedEvent(this));
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