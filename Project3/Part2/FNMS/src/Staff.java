// staff members have a name, access to store, and know how many days theyve worked in a row
// they can publish information to subscribers, and do so when arriving or leaving store
// can be extended to create Staff members with completely different roles
abstract class Staff extends Publisher implements Utility {
    protected Store store_;
    protected String name_;
    private int days_worked_ = 0;
    
    // methods to access staff members names and worked days stats
    public String GetName() { return name_; }
    public void IncrementDaysWorked() { days_worked_++; }
    public int GetDaysWorked() { return days_worked_; }
    public void ResetDaysWorked() { days_worked_ = 0; }

    protected void Publish(String context, int data) { Publish(context, name_, data); }
   
    // broadcast arriving to store
    public void ArriveAtStore() {
        Print(name_  + " has arrived at the store on Day " + Simulation.current_day_);
        Publish("arrival", 0);
    }

    // broadcast leaving
    public void CloseStore() {
        Print(name_ + " leaves the store");
        Publish("leftstore", 0);
    }
}