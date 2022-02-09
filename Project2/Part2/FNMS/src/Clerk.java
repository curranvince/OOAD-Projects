public class Clerk extends Staff {
    // This is an example of Encapsulation
    // Only the clerk has info about their break percentage
    // and can 'do' things with  it
    private int break_percentage_;

    Clerk(String name, int break_percentage) {
        name_ =  name;
        break_percentage_ = break_percentage;
    }

    boolean Clean() { return (Utility.GetRandomNum(100) > break_percentage_); }
}
