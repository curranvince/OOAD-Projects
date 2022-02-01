public class Clerk extends Staff {
    int break_percentage_;

    Clerk(String name, int break_percentage) {
        name_ =  name;
        break_percentage_ = break_percentage;
    }

    boolean Clean() { return (Utility.GetRandomNum(100) > break_percentage_); }
}
