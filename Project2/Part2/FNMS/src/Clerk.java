public class Clerk extends Staff {
    int break_percentage_;

    Clerk(String name, int break_percentage) {
        name_ =  name;
        break_percentage_ = break_percentage;
    }
    
    void ArriveAtStore(int day) { System.out.println(name_ + " has arrived at the store on Day " + day); }
}
