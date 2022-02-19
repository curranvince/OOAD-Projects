// TO DO DECORATE SELL METHOD
// https://www.geeksforgeeks.org/decorator-design-pattern-in-java-with-example/
// https://refactoring.guru/design-patterns/decorator/java/example
// This is an example of the Decorator pattern
import java.util.Set;

abstract class ClerkDecorator extends AbstractClerk {
    protected AbstractClerk decoratedStaff_;

    public ClerkDecorator(AbstractClerk decoratedStaff) { 
        this.decoratedStaff_ = decoratedStaff; 
    }
    // Staff methods
    public String GetName() { return decoratedStaff_.GetName(); }
    public void IncrementDaysWorked() { decoratedStaff_.IncrementDaysWorked(); }
    public int GetDaysWorked() { return decoratedStaff_.GetDaysWorked(); }
    public void ResetDaysWorked() { decoratedStaff_.ResetDaysWorked(); }
    // Publisher methods
    public void Subscribe(Subscriber subscriber) {  decoratedStaff_.Subscribe(subscriber); }
    public void Unsubscribe(Subscriber unsubscriber) { decoratedStaff_.Unsubscribe(unsubscriber); }
    // Clerk methods
    public void ArriveAtStore() { decoratedStaff_.ArriveAtStore(); }
    public boolean CheckRegister() { return decoratedStaff_.CheckRegister(); }
    public void GoToBank() { decoratedStaff_.GoToBank(); }
    public Set<Item.ItemType> DoInventory() { return decoratedStaff_.DoInventory(); }
    public int PlaceOrders(Set<Item.ItemType> orderTypes) { return decoratedStaff_.PlaceOrders(orderTypes); }
    public int Sell(Item item, int salePrice) { return decoratedStaff_.Sell(item, salePrice); }
    public int Buy(Item item, int salePrice) { return decoratedStaff_.Buy(item, salePrice); }
    public int TryTransaction(Item item, boolean buying) { return decoratedStaff_.TryTransaction(item, buying); }
    public int HandleCustomer(Customer customer) { return decoratedStaff_.HandleCustomer(customer); }
    public Item CheckForItem(Item.ItemType itemType) { return decoratedStaff_.CheckForItem(itemType); }
    public void CleanStore() { decoratedStaff_.CleanStore(); }
    public void CloseStore() { decoratedStaff_.CloseStore(); }
}

// concrete class to implement new methods/overwrites
class ClerkSellDecorator extends ClerkDecorator {
    public ClerkSellDecorator(AbstractClerk clerk) { 
        super(clerk); 
    }

// decorated sell method to sell accessories when a stringed instrument is sold
    public int HandleCustomer(Customer customer) {
        int result = super.HandleCustomer(customer);
        if ((result < 0) && (customer.GetItemType().ordinal() > 7) && (customer.GetItemType().ordinal() < 11)) {
            // if we just sold a stringed instrument
            int chances[] = {10,15,20,30};
            Item.ItemType types[] = { Item.ItemType.GIGBAG, Item.ItemType.PRACTICEAMPS, Item.ItemType.CABLES, Item.ItemType.STRINGS };
            // if the item was electric add chances by 10 each
            if (customer.item_.GetComponent(Electric.class) != null ) {
                for (int i = 0; i < chances.length; i++) { chances[i] += 10; }
            }
            // try selling accessories
            for (int i = 0; i < chances.length; i++) {
                if (GetRandomNum(100) < chances[i]) {
                    // for cables and strings try to sell 2 or 3
                    int num = 1;
                    if (i == 2) { num = GetRandomNum(1,3); }
                    else if (i == 3) { num = GetRandomNum(1, 4); }
                    for (int j = 0; j < num; j++) {
                        Print("The customer decides they also want to buy a " + types[i].name());
                        Item toSell = super.CheckForItem(types[i]);
                        if (toSell != null)  result += super.Sell(toSell, toSell.list_price_);
                    }
                }
            }
        }
        return result;
    }
}