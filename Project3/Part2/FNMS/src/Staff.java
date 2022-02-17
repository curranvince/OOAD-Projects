import java.util.*;
public interface Staff extends Utility {
    public String GetName();
    public void IncrementDaysWorked();
    public int GetDaysWorked();
    public void ResetDaysWorked();
    public void Subscribe(Subscriber subscriber);
    public void Unsubscribe(Subscriber unsubscriber);
    //public void Publish(String context, int data);
    public void ArriveAtStore();
    public boolean CheckRegister();
    public void GoToBank();
    public void Sell(Item item, int salePrice);
    public void Buy(Item item, int salePrice);
    public boolean TryToSell(Item item);
    public boolean TryToBuy(Item item);
    public Item CheckForItem(Item.ItemType itemType);
    public Set<Item.ItemType> DoInventory();
    public int PlaceOrders(Set<Item.ItemType> orderTypes);
    public void CleanStore();
    public void CloseStore();
}