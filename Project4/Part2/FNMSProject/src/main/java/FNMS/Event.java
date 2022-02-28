package FNMS;

abstract class MyEvent {
    protected int data_;
    protected Store store_;
    protected String name_;
    
    MyEvent(int data, Store store) {
        data_ = data;
        store_ = store;
        if (store_ != null) name_ = store_.GetActiveClerk().GetName();
    }

    public void update(int data) { data_ += data; }
    public Store GetStore() { return store_; }
    public int GetData() { return data_; }
    public String GetName() { return name_; }
    public int GetClerkID() { return store_.GetActiveClerk().GetID(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyEvent)) return false;
        MyEvent e = (MyEvent)o;
        if (e instanceof CreatedClerkEvent) { 
            if (e.GetName() == this.GetName()) return true;
            return false;
        }
        if (e.getClass() == this.getClass() && e.GetStore().getName() == this.GetStore().getName()) return true;
        return false;
    }
}

class ArrivalEvent extends MyEvent {
    public ArrivalEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " arrived at the " + store_.getName()); }
}

class ItemsAddedEvent extends MyEvent {
    public ItemsAddedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " added " + data_ + " item(s) to " + store_.getName() + "'s inventory"); }
}

class RegisterEvent extends MyEvent {
    public RegisterEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " checked the " + store_.getName() + "'s register to find $" + data_); }
}

class BankEvent extends MyEvent {
    public BankEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " went to the bank, then checked the " + store_.getName() + "'s register to find $" + data_); }
}

class InventoryEvent extends MyEvent {
    public InventoryEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " counted " + data_ + " item(s) in " + store_.getName() + "'s' inventory"); }
}

class InventoryValueEvent extends MyEvent {
    public InventoryValueEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " found the " + store_.getName() + "'s' inventory is worth $" + data_); }
}

class BrokeTuningEvent extends MyEvent {
    public BrokeTuningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " damaged " + data_ + " item(s) at the " + store_.getName() + " while tuning"); }
}

class ItemsOrderedEvent extends MyEvent {
    public ItemsOrderedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " ordered " + data_ + " item(s) for the " + store_.getName()); }
}

class ItemsSoldEvent extends MyEvent {
    public ItemsSoldEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " sold " + data_ + " item(s) at the "+ store_.getName()); }
}

class ItemsBoughtEvent extends MyEvent {
    public ItemsBoughtEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " bought " + data_ + " item(s) at the "+ store_.getName()); }
}

class BrokeCleaningEvent extends MyEvent {
    public BrokeCleaningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " damaged " + data_ + " item(s) at the " + store_.getName() + " while cleaning"); }
}

class LeaveEvent extends MyEvent {
    public LeaveEvent(Store store) { super(0, store); }
    public String toString() { return (name_ + " left the " + store_.getName()); }
}

class ClosedEvent extends MyEvent {
    public ClosedEvent(Store store) { super(0, store); }
    public String toString() { return ("The " + store_.getName() + " was closed today"); }
}

class CreatedClerkEvent extends MyEvent implements Utility {
    public CreatedClerkEvent(String name) {
        super(0,null);
        name_ = name;
    }
}