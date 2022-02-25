package FNMS;

import java.util.Set;

// staff members have a name, access to store, and know how many days theyve worked in a row
// they can publish information to subscribers, and do so when arriving or leaving store
// can be extended to create Staff members with completely different roles
abstract class Staff extends Publisher {
    protected Store store_;
    protected String name_;
    private int days_worked_ = 0;
    
    // methods to access staff members names and worked days stats
    public String GetName() { return name_; }
    public void IncrementDaysWorked() { days_worked_++; }
    public int GetDaysWorked() { return days_worked_; }
    public void ResetDaysWorked() { days_worked_ = 0; }

    public void UpdateStore(Store store) { store_ = store; }
    
    protected void Publish(String context, int data) { super.Publish(context, name_, data); }

    // broadcast arriving to store
    public void ArriveAtStore() {
        days_worked_++;
        Print(name_  + " has arrived at the " + store_.getName() + " on Day " + Simulation.current_day_);
        Publish("arrival", 1);
    }

    // broadcast leaving
    public void CloseStore() {
        Print(name_ + " leaves the " + store_.getName());
        Publish("leftstore", 0);
    }
}

// break percentage and tune strategy are examples of encapsulation
// those data members are hidden from classes which do not inherit AbstractClerk
abstract class AbstractClerk extends Staff {
    protected int break_percentage_;
    protected TuneStrategy tune_strategy_;

    abstract public boolean CheckRegister();
    abstract public void GoToBank();
    abstract public Set<Item.ItemType> DoInventory();
    abstract public void PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public boolean Sell(Item item, int salePrice);
    abstract public boolean Buy(Item item, int salePrice);
    abstract public boolean OfferAccepted(Item item, boolean buying, boolean discount);
    abstract public Item CheckForItem(Item.ItemType itemType);
    abstract public boolean TryTransaction(Item item, boolean buying);
    abstract public void CleanStore();
    abstract public String GetTime();
}

// The Tune interface and its subclasses is an example of the Strategy pattern
interface TuneStrategy extends Utility { public int Execute(Tuneable tuneable); }

class ManualTune implements TuneStrategy {
    // 80% chance to tune, 20% chance to untune
    public int Execute(Tuneable tuneable) { 
        if (!tuneable.IsTuned()) {
            if (GetRandomNum(10) > 1) {
                tuneable.Tune();
                return 1;
            }
        } else {
            if (GetRandomNum(10) > 7) {
                tuneable.Untune();
                return -1;
            }
        }
        return 0;
    }
}

class HaphazardTune implements TuneStrategy {
    // 50% chance to flip tune
    public int Execute(Tuneable tuneable) { 
        if (GetRandomNum(2) == 0) {
            if (tuneable.IsTuned()) {
                tuneable.Untune();
                return -1;
            } else {
                tuneable.Tune();
                return 1;
            }
        }
        return 0;
    }
}

class ElectronicTune implements TuneStrategy {
    // automatically tune
    public int Execute(Tuneable tuneable) { 
        if (!tuneable.IsTuned()) {
            tuneable.Tune();
            return 1;
        }
        return 0;
    }
}

