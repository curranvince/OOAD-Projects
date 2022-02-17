import java.util.Set;

abstract class AbstractClerk extends Staff {
    abstract public boolean CheckRegister();
    abstract public void GoToBank();
    abstract public Item CheckForItem(Item.ItemType itemType);
    abstract public boolean TryToSell(Item item);
    abstract public boolean TryToBuy(Item item);
    abstract public Set<Item.ItemType> DoInventory();
    abstract public int PlaceOrders(Set<Item.ItemType> orderTypes);
    abstract public void CleanStore();
}