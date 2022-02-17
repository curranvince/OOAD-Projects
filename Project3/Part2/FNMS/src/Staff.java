import java.util.*;
abstract class Staff extends Publisher implements Utility {
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

    public boolean CheckRegister() {
        Publish("checkedregister", Store.register_.GetAmount());
        // broadcast register amount and return if its greater than 75 or not
        Print(name_ + " checks the register to find $" + Store.register_.GetAmount());
        return (Store.register_.GetAmount() >= 75) ? true : false;
    }

    public void GoToBank() {
        // add 1000 to register and broadcast
        Print(name_ + " goes to the bank to withdraw $1000 for the register" );
        Store.register_.AddMoney(1000);
        Store.total_withdrawn_ += 1000;
        Publish("checkedregister", Store.register_.GetAmount());
    }

    protected Item CheckForItem(Item.ItemType itemType) {
        for (Item item : Store.inventory_) {
            if (item.itemType == itemType) return item;
        }
        return null;
    }

    // sell an item to a customer
    protected void Sell(Item item, int salePrice) {
        Print("The customer buys the " + item.name_ + " for $" + salePrice);
        // add money to register, update item stats, update inventories
        Store.register_.AddMoney(salePrice);
        item.day_sold_ = Simulation.current_day_;
        item.sale_price_ = salePrice;
        Store.inventory_.remove(item);
        Store.sold_.add(item);
    }

    // buy an item from a customer
    protected void Buy(Item item, int salePrice) {
        Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + salePrice);
        // take money from register, update item stats, update inventories
        Store.register_.TakeMoney(salePrice);
        item.purchase_price_ = salePrice;
        item.list_price_ = salePrice*2;
        item.day_arrived = Simulation.current_day_;
        Store.inventory_.add(item);
    }

    // broadcast leaving
    public void CloseStore() {
        Print(name_ + " locks up and goes home for the night");
        Publish("leftstore", 0);
    }
    
    abstract public boolean TryToSell(Item item);
    abstract public boolean TryToBuy(Item item);
    abstract public Set<Item.ItemType> DoInventory();
    abstract public int PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public void CleanStore();
}