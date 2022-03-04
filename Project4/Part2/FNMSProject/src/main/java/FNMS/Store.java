package FNMS;

import java.util.*;

import FNMS.Item.ItemType;

// Publishers have a list of subscribers which can be subscribed/unsubscribed to
// They can also publish information to their subscribers
abstract class Publisher implements Utility {
    protected void Publish(MyEvent event) { Subscriber.Update(event); }
}

class Store extends Publisher {
    private String name_;
    private int total_withdrawn_ = 0;
    private AbstractClerk activeClerk_;
    private LinkedList<Customer> customers_ = new LinkedList<Customer>();

    public KitFactory kitFactory_;
    public CashRegister register_ = new CashRegister();
    public List<Item> inventory_ = new ArrayList<Item>();
    public List<Item> sold_ = new ArrayList<Item>();
    public List<ItemType> discontinued_ = new ArrayList<ItemType>();
    public HashMap<Integer, List<ItemType>> orders_ = new HashMap<Integer, List<ItemType>>();

    // CashRegister class to handle the Stores $
    // Good example of Cohesion because the class has
    // one specifc purpose (handling money/doing simple math)
    class CashRegister {
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

    Store(String name, KitFactory kitFactory) {
        kitFactory_ = kitFactory;
        name_ = name;
        // Stores start with 3 of each item
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

    // store opens for the day
    public void Opens() {
        Print(activeClerk_.GetName() + " lets customers into the " + name_);
        // loop through queue of customers allowing them all to make requests
        while (!customers_.isEmpty()) { // https://stackoverflow.com/questions/57715470/iterating-a-list-until-the-list-is-empty
            Iterator<Customer> it = customers_.listIterator();
            while (it.hasNext()) {
                Customer customer = it.next();
                customer.MakeRequest();
                customer.LeaveStore();
                it.remove();
            }
        }
        Print("The line at the " + name_ + " has finally seceded");
    }

    // announce that the store is closed (for Sunday)
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