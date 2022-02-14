// The Tune interface and its subclasses is an example of the Strategy pattern. 
interface Tune { public void Tune(Item item); }

class ManualTune implements Tune {
    // 80% chance to tune, 20% chance to untune
    public void Tune(Item item) { 
        if (Utility.GetRandomNum(10) < 8) {
            item.Tune();
        }  else {
            item.Untune(); 
        } 
    }
}

class HaphazardTune implements Tune {
    // 50% chance to flip tune
    public void Tune(Item item) { if (Utility.GetRandomNum(2) == 0) item.FlipTune(); }
}

class ElectronicTune implements Tune {
    // automatically tune
    public void Tune(Item item) { item.Tune(); }
}

abstract class Staff {
    String name_;
    int days_worked_ = 0;

    public void ResetDaysWorked() { days_worked_ = 0; }
    public void IncrementDaysWorked() { days_worked_ += 1; }
    public boolean Clean() { return true; }
}

class Clerk extends Staff {
    // This is an example of Encapsulation
    // Only the clerk has info about their break percentage
    // and can 'do' things with  it
    private int break_percentage_;
    private Tune tune_;

    Clerk(String name, int break_percentage, Tune tune) {
        name_ =  name;
        break_percentage_ = break_percentage;
        tune_ = tune;
    }

    public boolean Clean() { return (Utility.GetRandomNum(100) > break_percentage_); }

    public void Tune(Item item) { tune_.Tune(item); }
}
