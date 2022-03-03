package FNMS;

abstract class MyEvent implements Utility, Cloneable {
    protected int data_;
    protected Store store_;
    protected String name_;
    
    MyEvent(int data, Store store) {
        data_ = data;
        store_ = store;
        if (store_ != null) name_ = store_.GetActiveClerk().GetName();
    }

    public MyEvent(MyEvent another) {
        data_ = another.GetData();
        store_ = another.GetStore();
        name_ = another.GetName();
    }

    public Store GetStore() { return store_; }
    public int GetData() { return data_; }
    public String GetName() { return name_; }
    public int GetClerkID() { return store_.GetActiveClerk().GetID(); }
    public void UpdateData(int data) { data_ += data; }
    
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

    @Override       // https://stackoverflow.com/questions/52377601/java-cloning-with-super-and-sub-classes
    public MyEvent clone() {
        try {
            return (MyEvent)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class ArrivalEvent extends MyEvent {
    public ArrivalEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " arrived at the " + store_.getName()); }
    @Override
    public ArrivalEvent clone() {
        return (ArrivalEvent)super.clone();
    }
}

class ItemsAddedEvent extends MyEvent {
    public ItemsAddedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " added " + data_ + " item(s) to " + store_.getName() + "'s inventory"); }
    @Override
    public ItemsAddedEvent clone() {
        return (ItemsAddedEvent)super.clone();
    }
}

class RegisterEvent extends MyEvent {
    public RegisterEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " checked the " + store_.getName() + "'s register to find $" + data_); }
    @Override
    public RegisterEvent clone() {
        return (RegisterEvent)super.clone();
    }
}

class BankEvent extends MyEvent {
    public BankEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " went to the bank, then checked the " + store_.getName() + "'s register to find $" + data_); }
    @Override
    public BankEvent clone() {
        return (BankEvent)super.clone();
    }
}

class InventoryEvent extends MyEvent {
    public InventoryEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " counted " + data_ + " item(s) in " + store_.getName() + "'s' inventory"); }
    @Override
    public InventoryEvent clone() {
        return (InventoryEvent)super.clone();
    }
}

class InventoryValueEvent extends MyEvent {
    public InventoryValueEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " found the " + store_.getName() + "'s' inventory is worth $" + data_); }
    @Override
    public InventoryValueEvent clone() {
        return (InventoryValueEvent)super.clone();
    }
}

class BrokeTuningEvent extends MyEvent {
    public BrokeTuningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " damaged " + data_ + " item(s) at the " + store_.getName() + " while tuning"); }
    @Override
    public BrokeTuningEvent clone() {
        return (BrokeTuningEvent)super.clone();
    }
}

class ItemsOrderedEvent extends MyEvent {
    public ItemsOrderedEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " ordered " + data_ + " item(s) for the " + store_.getName()); }
    @Override
    public ItemsOrderedEvent clone() {
        return (ItemsOrderedEvent)super.clone();
    }
}

class ItemsSoldEvent extends MyEvent {
    public ItemsSoldEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " sold " + data_ + " item(s) at the "+ store_.getName()); }
    @Override
    public ItemsSoldEvent clone() {
        return (ItemsSoldEvent)super.clone();
    }
}

class SalePriceEvent extends MyEvent {
    public SalePriceEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " sold $" + data_ + " worth of items at the "+ store_.getName()); }
    @Override
    public SalePriceEvent clone() {
        return (SalePriceEvent)super.clone();
    }
}

class ItemsBoughtEvent extends MyEvent {
    public ItemsBoughtEvent(Store store) { super(1, store); }
    public String toString() { return (name_ + " bought " + data_ + " item(s) at the "+ store_.getName()); }
    @Override
    public ItemsBoughtEvent clone() {
        return (ItemsBoughtEvent)super.clone();
    }
}

class BrokeCleaningEvent extends MyEvent {
    public BrokeCleaningEvent(int data, Store store) { super(data, store); }
    public String toString() { return (name_ + " damaged " + data_ + " item(s) at the " + store_.getName() + " while cleaning"); }
    @Override
    public BrokeCleaningEvent clone() {
        return (BrokeCleaningEvent)super.clone();
    }
}

class LeaveEvent extends MyEvent {
    public LeaveEvent(Store store) { super(0, store); }
    public String toString() { return (name_ + " left the " + store_.getName()); }
    @Override
    public LeaveEvent clone() {
        return (LeaveEvent)super.clone();
    }
}

class ClosedEvent extends MyEvent {
    public ClosedEvent(Store store) { super(0, store); }
    public String toString() { return ("The " + store_.getName() + " was closed today"); }
    @Override
    public ClosedEvent clone() {
        return (ClosedEvent)super.clone();
    }
}

class CreatedClerkEvent extends MyEvent {
    public CreatedClerkEvent(String name) {
        super(0,null);
        name_ = name;
    }
    @Override
    public CreatedClerkEvent clone() {
        return (CreatedClerkEvent)super.clone();
    }
}

class EODRegisterEvent extends MyEvent {
    public EODRegisterEvent(int data, Store store) { super(data, store); }
    @Override
    public EODRegisterEvent clone() {
        return (EODRegisterEvent)super.clone();
    }
}