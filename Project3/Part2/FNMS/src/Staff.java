// The Tune interface and its subclasses is an example of the Strategy pattern. 
interface Tune extends Utility { public int Tune(Item item); }

class ManualTune implements Tune {
    // 80% chance to tune, 20% chance to untune
    public int Tune(Item item) { 
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

class HaphazardTune implements Tune {
    // 50% chance to flip tune
    public int Tune(Item item) { 
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

class ElectronicTune implements Tune {
    // automatically tune
    public int Tune(Item item) { 
        if (!item.IsTuned()) {
            item.Tune();
            return 1;
        }
        return 0;
    }
}

abstract class Staff implements Utility {
    String name_;
    private int days_worked_ = 0;

    Staff(String name) { name_ = name; }
    public void ResetDaysWorked() { days_worked_ = 0; }
    public void IncrementDaysWorked() { days_worked_ += 1; }
    public int GetDaysWorked() { return days_worked_; }
    public boolean Clean() { return true; }
    public boolean Tune(Item item) { return true; }
}

class Clerk extends Staff {
    // This is an example of Encapsulation
    // Only the clerk has info about their break percentage
    // and can 'do' things with  it
    private int break_percentage_;
    private Tune tune_;

    Clerk(String name, int break_percentage, Tune tune) {
        super(name);
        break_percentage_ = break_percentage;
        tune_ = tune;
    }

    public boolean Clean() { 
        Print("The store closes for the day and " + name_ + " begins cleaning");
        return (GetRandomNum(100) > break_percentage_); 
    }

    public boolean Tune(Item item) { 
        Print(name_ + " is attempting to tune the " + item.name_);
        switch (tune_.Tune(item)) {
            case -1:
                Print(name_ + " has done a bad job tuning, and untuned the " + item.name_);
                if (GetRandomNum(10) == 0) {
                    Print(name_ + " has done such a bad job tuning they damaged the item");
                    if (item.LowerCondition() == false) item = null;
                }
                return false;
            case 0:
                Print(name_ + " has not changed the state of the " + item.name_ + ", it is still " + (item.IsTuned() ? "tuned" : "untuned"));
                return true;
            case 1:
                Print(name_ + " has successfully tuned the " + item.name_);
                return true;
            default:
                Print("Error: Tune returned bad value");
                return true;
        }
    }
}
