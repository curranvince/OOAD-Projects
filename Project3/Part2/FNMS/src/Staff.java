import java.util.*;
abstract class Staff extends Publisher implements Utility {
    protected Store store_;
    protected String name_;
    private int days_worked_ = 0;
    
    public String GetName() { return name_; }
    public void IncrementDaysWorked() { days_worked_++; }
    public int GetDaysWorked() { return days_worked_; }
    public void ResetDaysWorked() { days_worked_ = 0; }

    protected void Publish(String context, int data) { Publish(context, name_, data); }
   
    public void ArriveAtStore() {
        Publish("arrival", 0);
        Print(name_  + " has arrived at the store on Day " + Simulation.current_day_); 
    }

    // broadcast leaving
    public void CloseStore() {
        Print(name_ + " locks up and goes home for the night");
        Publish("leftstore", 0);
    }

    abstract public boolean CheckRegister();
    abstract public void GoToBank();
    abstract public Item CheckForItem(Item.ItemType itemType);
    abstract public boolean TryToSell(Item item);
    abstract public boolean TryToBuy(Item item);
    abstract public Set<Item.ItemType> DoInventory();
    abstract public int PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public void CleanStore();
}