package FNMS;

import java.util.Set;

// The Tune interface and its subclasses is an example of the Strategy pattern. 
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

// break percentage and tune strategy are examples of encapsulation
// those data members are hidden from classes which do not inherit AbstractClerk
abstract class AbstractClerk extends Staff {
    protected int break_percentage_;
    protected TuneStrategy tune_strategy_;

    abstract public boolean CheckRegister();
    abstract public void GoToBank();
    abstract public Set<Item.ItemType> DoInventory();
    abstract public int PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public int Sell(Item item, int salePrice);
    abstract public int Buy(Item item, int salePrice);
    abstract public Item CheckForItem(Item.ItemType itemType);
    abstract public int TryTransaction(Item item, boolean buying);
    abstract public int HandleCustomer(Customer customer);
    abstract public void CleanStore();
}