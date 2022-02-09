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

    Clerk(String name, int break_percentage) {
        name_ =  name;
        break_percentage_ = break_percentage;
    }

    public boolean Clean() { return (Utility.GetRandomNum(100) > break_percentage_); }
}