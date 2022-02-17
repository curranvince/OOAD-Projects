// TO DO DECORATE SELL METHOD
// https://www.geeksforgeeks.org/decorator-design-pattern-in-java-with-example/
// https://refactoring.guru/design-patterns/decorator/java/example
import java.util.*;
import java.io.*;

abstract class StaffDecorator implements Staff {
    protected Staff decoratedStaff_;

    public StaffDecorator(Staff decoratedStaff) { 
        this.decoratedStaff_ = decoratedStaff; 
    }
    
    public String GetName() { return decoratedStaff_.GetName(); }
    public void IncrementDaysWorked() { decoratedStaff_.IncrementDaysWorked(); }
    public int GetDaysWorked() { return decoratedStaff_.GetDaysWorked(); }
    public void ResetDaysWorked() { decoratedStaff_.ResetDaysWorked(); }
    public void Subscribe(Subscriber subscriber) { decoratedStaff_.Subscribe(subscriber); }
    public void Unsubscribe(Subscriber unsubscriber) { decoratedStaff_.Unsubscribe(unsubscriber); }
    public void ArriveAtStore() { decoratedStaff_.ArriveAtStore(); }
    public boolean CheckRegister() { return decoratedStaff_.CheckRegister(); }
    public void GoToBank() { decoratedStaff_.GoToBank(); }
    public void Sell(Item item, int salePrice) { decoratedStaff_.Sell(item, salePrice); }
    public void Buy(Item item, int salePrice) { decoratedStaff_.Buy(item, salePrice); }
    public boolean TryToSell(Item item) { return decoratedStaff_.TryToSell(item); }
    public boolean TryToBuy(Item item) { return decoratedStaff_.TryToBuy(item); }
    public Item CheckForItem(Item.ItemType itemType) { return decoratedStaff_.CheckForItem(itemType); }
    public Set<Item.ItemType> DoInventory() { return decoratedStaff_.DoInventory(); }
    public int PlaceOrders(Set<Item.ItemType> orderTypes) { return decoratedStaff_.PlaceOrders(orderTypes); }
    public void CleanStore() { decoratedStaff_.CleanStore(); }
    public void CloseStore() { decoratedStaff_.CloseStore(); }
}

class ClerkSellDecorator extends StaffDecorator {
    public ClerkSellDecorator(Staff staff) { 
        super(staff); 
    }

    public String GetName() { return super.GetName(); }
    public void IncrementDaysWorked() { super.IncrementDaysWorked(); }
    public int GetDaysWorked() { return super.GetDaysWorked(); }
    public void ResetDaysWorked() { super.ResetDaysWorked(); }
    public void Subscribe(Subscriber subscriber) {  super.Subscribe(subscriber); }
    public void Unsubscribe(Subscriber unsubscriber) { super.Unsubscribe(unsubscriber); }
    public void ArriveAtStore() { super.ArriveAtStore(); }
    public boolean CheckRegister() { return super.CheckRegister(); }
    public void GoToBank() { super.GoToBank(); }
    public void Sell(Item item, int salePrice) { super.Sell(item, salePrice); }
    
    public boolean TryToSell(Item item) { 
        if (super.TryToSell(item)) {
// TO DO
// make extra attempts to sell depending on itemtype
            if (item.itemType == Item.ItemType.GUITAR) {
            
                return true;
            } else {
                return true;
            }
        }
        return false;
    }
    
    public boolean TryToBuy(Item item) { return super.TryToBuy(item); }
    public void Buy(Item item, int salePrice) { super.Buy(item, salePrice); }
    public Item CheckForItem(Item.ItemType itemType) { return super.CheckForItem(itemType); }
    public Set<Item.ItemType> DoInventory() { return super.DoInventory(); }
    public int PlaceOrders(Set<Item.ItemType> orderTypes) { return super.PlaceOrders(orderTypes); }
    public void CleanStore() { super.CleanStore(); }
    public void CloseStore() { super.CloseStore(); }
    
}