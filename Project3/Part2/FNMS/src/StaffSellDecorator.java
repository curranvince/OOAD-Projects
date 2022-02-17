// TO DO DECORATE SELL METHOD
// https://www.geeksforgeeks.org/decorator-design-pattern-in-java-with-example/
// https://refactoring.guru/design-patterns/decorator/java/example
import java.util.*;
import java.io.*;

abstract class StaffDecorator extends Staff {
    protected Staff decoratedStaff_;

    public StaffDecorator(Staff decoratedStaff) { 
        this.decoratedStaff_ = decoratedStaff; 
    }
    
    public String GetName() { return decoratedStaff_.GetName(); }
    public void IncrementDaysWorked() { decoratedStaff_.IncrementDaysWorked(); }
    public int GetDaysWorked() { return decoratedStaff_.GetDaysWorked(); }
    public void ResetDaysWorked() { decoratedStaff_.ResetDaysWorked(); }
    public void Subscribe(Subscriber subscriber) {  decoratedStaff_.Subscribe(subscriber); }
    public void Unsubscribe(Subscriber unsubscriber) { decoratedStaff_.Unsubscribe(unsubscriber); }
    public void ArriveAtStore() { decoratedStaff_.ArriveAtStore(); }
    public boolean CheckRegister() { return decoratedStaff_.CheckRegister(); }
    public void GoToBank() { decoratedStaff_.GoToBank(); }
    public Item CheckForItem(Item.ItemType itemType) { return decoratedStaff_.CheckForItem(itemType); }
    public boolean TryToSell(Item item) { return decoratedStaff_.TryToSell(item); }
    public boolean TryToBuy(Item item) { return decoratedStaff_.TryToBuy(item); }
    public Set<Item.ItemType> DoInventory() { return decoratedStaff_.DoInventory(); }
    public int PlaceOrders(Set<Item.ItemType> orderTypes) { return decoratedStaff_.PlaceOrders(orderTypes); }
    public void CleanStore() { decoratedStaff_.CleanStore(); }
    public void CloseStore() { decoratedStaff_.CloseStore(); }
}

class StaffSellDecorator extends StaffDecorator {
    public StaffSellDecorator(Staff staff) { 
        super(staff); 
    }
    
    public boolean TryToSell(Item item) { 
        if (super.TryToSell(item)) {
// TO DO
// make extra attempts to sell depending on itemtype
            if (item.itemType == Item.ItemType.GUITAR) {
                //System.out.println("STILL DECORATED");
                return true;
            } else {
                return true;
            }
        }
        return false;
    }
}