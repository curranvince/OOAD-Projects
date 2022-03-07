package FNMS;

abstract class MyEvent implements Cloneable {
    protected int data_;
    protected Store store_;
    protected String clerk_name_;
    
    MyEvent(int data, Store store) {
        data_ = data;
        store_ = store;
        if (store_ != null) clerk_name_ = store_.GetActiveClerk().GetName();
    }

    public Store GetStore() { return store_; }
    public int GetData() { return data_; }
    public String GetClerkName() { return clerk_name_; }
    public int GetClerkID() { return store_.GetActiveClerk().GetID(); }
    public void UpdateData(int data) { data_ += data; }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyEvent)) return false;
        MyEvent e = (MyEvent)o;
        if (e instanceof CreatedClerkEvent) { 
            if (e.GetClerkName() == this.GetClerkName()) return true;
            return false;
        }
        if (e.getClass() == this.getClass() && e.GetStore().toString() == this.GetStore().toString()) return true;
        return false;
    }
}

class ArrivalEvent extends MyEvent {
    public ArrivalEvent(Store store) { super(1, store); }
    public String toString() { return (clerk_name_ + " arrived at the " + store_.toString()); }
}

class ItemsAddedEvent extends MyEvent {
    public ItemsAddedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " added " + data_ + " item(s) to " + store_.toString() + "'s inventory"); }
}

class RegisterEvent extends MyEvent {
    public RegisterEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " checked the " + store_.toString() + "'s register to find $" + data_); }
}

class BankEvent extends MyEvent {
    public BankEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " went to the bank, then checked the " + store_.toString() + "'s register to find $" + data_); }
}

class InventoryEvent extends MyEvent {
    public InventoryEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " counted " + data_ + " item(s) in " + store_.toString() + "'s' inventory"); }
}

class InventoryValueEvent extends MyEvent {
    public InventoryValueEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " found the " + store_.toString() + "'s' inventory is worth $" + data_); }
}

class BrokeTuningEvent extends MyEvent {
    public BrokeTuningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " damaged " + data_ + " item(s) at the " + store_.toString() + " while tuning"); }
}

class ItemsOrderedEvent extends MyEvent {
    public ItemsOrderedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " ordered " + data_ + " item(s) for the " + store_.toString()); }
}

class ItemsSoldEvent extends MyEvent {
    public ItemsSoldEvent(Store store) { super(1, store); }
    public String toString() { return (clerk_name_ + " sold " + data_ + " item(s) at the "+ store_.toString()); }
}

class SalePriceEvent extends MyEvent {
    public SalePriceEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " sold $" + data_ + " worth of items at the "+ store_.toString()); }
}

class ItemsBoughtEvent extends MyEvent {
    public ItemsBoughtEvent(Store store) { super(1, store); }
    public String toString() { return (clerk_name_ + " bought " + data_ + " item(s) at the "+ store_.toString()); }
}

class BrokeCleaningEvent extends MyEvent {
    public BrokeCleaningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (clerk_name_ + " damaged " + data_ + " item(s) at the " + store_.toString() + " while cleaning"); }
}

class LeaveEvent extends MyEvent {
    public LeaveEvent(Store store) { super(0, store); }
    public String toString() { return (clerk_name_ + " left the " + store_.toString()); }
}

class ClosedEvent extends MyEvent {
    public ClosedEvent(Store store) { super(0, store); }
    public String toString() { return ("The " + store_.toString() + " was closed today"); }
}

class CreatedClerkEvent extends MyEvent {
    public CreatedClerkEvent(String name) {
        super(0,null);
        clerk_name_ = name;
    }
}

class EODRegisterEvent extends MyEvent {
    public EODRegisterEvent(int data, Store store) { super(data, store); } 
}