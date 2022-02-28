package FNMS;

import FNMS.Item.ItemType;

abstract class Request implements Utility {
    abstract void Execute();
}

class BuyRequest extends Request {
    private AbstractClerk clerk_;
    private Customer customer_;
    private ItemType itemType_;

    public BuyRequest(AbstractClerk aclerk, Customer customer, ItemType itemType) {
        clerk_ = aclerk;
        customer_ = customer;
        itemType_ = itemType;
    }

    public BuyRequest(AbstractClerk aclerk, Customer customer) {
        clerk_ = aclerk;
        customer_ = customer;
        itemType_ = GetRandomEnumVal(ItemType.class);
    }

    public void Execute() {
        Item item = clerk_.CheckForItem(itemType_);
        if (item != null) {
            if (clerk_.TryTransaction(customer_, item, false) && customer_ instanceof User) {
                User user = (User)customer_;
                user.AddToInventory(item);
            }
        }
    }

    public String toString() { return "buy a " + itemType_.name(); }
}

class SellRequest extends Request {
    private AbstractClerk clerk_;
    private Customer customer_;
    private Item item_;

    public SellRequest(AbstractClerk aclerk, Customer customer, Item item) { 
        clerk_ = aclerk;
        customer_ = customer;
        item_ = item;
    }

    public SellRequest(AbstractClerk aclerk, Customer customer) {
        clerk_ = aclerk;
        customer_ = customer;
        item_ = ItemFactory.MakeItem(GetRandomEnumVal(ItemType.class).name());
    }

    public void Execute() {
        if (customer_ instanceof User && clerk_.TryTransaction(customer_, item_, true)) {
            User user = (User)customer_;
            user.RemoveFromInventory(item_);
        }
    }

    public String toString() { return "sell a " + item_.name_; }
}

class NameRequest extends Request {
    private AbstractClerk clerk_;

    public NameRequest(AbstractClerk clerk) { clerk_ = clerk; }

    public void Execute() {
        Print("The clerk says their name is " + clerk_.GetName());
    }

    public String toString() { return "find out the clerks name"; }
}

class TimeRequest extends Request {
    private AbstractClerk clerk_;

    public TimeRequest(AbstractClerk clerk) { clerk_ = clerk; }

    public void Execute() {
        Print("The clerk says its " + clerk_.GetTime());
    }

    public String toString() { return "find out the time"; }
}

class SwitchRequest extends Request {
    Simulation simulation_;
    User user_;

    public SwitchRequest(Simulation simulation, User user) {
        simulation_ = simulation;
        user_ = user;
    }

    public void Execute() {
        Print("The customer wants to switch stores");
        user_.SetStore(simulation_.GetStore());
    }

    public String toString() { return "switch stores"; }
}

class BuyKitRequest extends Request {
    Store store_;
    User user_;

    public BuyKitRequest(Store store, User user) {
        store_ = store;
        user_ = user;
    }

    public void Execute() {
        user_.AddToInventory(store_.GetActiveClerk().SellGuitarKit());
    }

    public String toString() { return "buy a guitar kit"; }
}

class LeaveRequest extends Request {
    User user_;

    public LeaveRequest(User user) { user_ = user; }

    public void Execute() { 
        user_.Leaves(); 
    }

    public String toString() { return "to leave"; }
}