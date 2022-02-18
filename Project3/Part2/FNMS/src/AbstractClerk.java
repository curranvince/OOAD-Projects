import java.util.Set;

// The Tune interface and its subclasses is an example of the Strategy pattern. 
interface TuneStrategy extends Utility { public int Execute(Item item); }

class ManualTune implements TuneStrategy {
    // 80% chance to tune, 20% chance to untune
    public int Execute(Item item) { 
        if (!item.IsTuned()) {
            if (GetRandomNum(10) > 1) {
                item.Tune();
                return 1;
            }
        } else {
            if (GetRandomNum(10) > 7) {
                item.Untune();
                return -1;
            }
        }
        return 0;
    }
}

class HaphazardTune implements TuneStrategy {
    // 50% chance to flip tune
    public int Execute(Item item) { 
        if (GetRandomNum(2) == 0) {
            if (item.IsTuned()) {
                item.Untune();
                return -1;
            } else {
                item.Tune();
                return 1;
            }
        }
        return 0;
    }
}

class ElectronicTune implements TuneStrategy {
    // automatically tune
    public int Execute(Item item) { 
        if (!item.IsTuned()) {
            item.Tune();
            return 1;
        }
        return 0;
    }
}

// extended by both Clerk and ClerkDecorator
abstract class AbstractClerk extends Staff {
    protected int break_percentage_;
    protected TuneStrategy tune_strategy_;

    abstract public boolean CheckRegister();
    abstract public void GoToBank();
    abstract public Set<Item.ItemType> DoInventory();
    abstract public int PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public int HandleCustomer(Customer customer);
    abstract public void CleanStore();
}