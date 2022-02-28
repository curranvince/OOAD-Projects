package FNMS;

import java.util.Set;

import FNMS.Item.ItemType;

// https://www.geeksforgeeks.org/decorator-design-pattern-in-java-with-example/
// https://refactoring.guru/design-patterns/decorator/java/example
// This is an example of the Decorator pattern (both classes in this file make it up)
// MUST implement ALL methods of AbstractClerk 
abstract class ClerkDecorator extends AbstractClerk {
    protected AbstractClerk decoratedStaff_;

    public ClerkDecorator(AbstractClerk decoratedStaff) { 
        this.decoratedStaff_ = decoratedStaff; 
    }
    // Publisher methods
    public void Subscribe(Subscriber subscriber) {  decoratedStaff_.Subscribe(subscriber); }
    public void Unsubscribe(Subscriber unsubscriber) { decoratedStaff_.Unsubscribe(unsubscriber); }
    public void Publish(MyEvent event) { decoratedStaff_.Publish(event); }
    // Staff methods
    public String GetName() { return decoratedStaff_.GetName(); }
    public int GetID() { return decoratedStaff_.GetID(); }
    public void IncrementDaysWorked() { decoratedStaff_.IncrementDaysWorked(); }
    public int GetDaysWorked() { return decoratedStaff_.GetDaysWorked(); }
    public void ResetDaysWorked() { decoratedStaff_.ResetDaysWorked(); }
    public void UpdateStore(Store store) { decoratedStaff_.UpdateStore(store); }
    // Clerk methods
    public void ArriveAtStore() { decoratedStaff_.ArriveAtStore(); }
    public boolean CheckRegister() { return decoratedStaff_.CheckRegister(); }
    public void GoToBank() { decoratedStaff_.GoToBank(); }
    public Set<ItemType> DoInventory() { return decoratedStaff_.DoInventory(); }
    public void PlaceOrders(Set<ItemType> orderTypes) { decoratedStaff_.PlaceOrders(orderTypes); }
    public boolean Sell(Item item, int salePrice) { return decoratedStaff_.Sell(item, salePrice); }
    public boolean Buy(Item item, int salePrice) { return decoratedStaff_.Buy(item, salePrice); }
    public Item CheckForItem(ItemType itemType) { return decoratedStaff_.CheckForItem(itemType); }
    public boolean TryTransaction(Customer customer, Item item, boolean buying) { return decoratedStaff_.TryTransaction(customer, item, buying); }
    public void CleanStore() { decoratedStaff_.CleanStore(); }
    public void CloseStore() { decoratedStaff_.CloseStore(); }
    public String GetTime() { return decoratedStaff_.GetTime(); }
    public Item SellGuitarKit() { return decoratedStaff_.SellGuitarKit(); }
}

// concrete class to implement new methods/overwrites
class ClerkSellDecorator extends ClerkDecorator {
    public ClerkSellDecorator(AbstractClerk clerk) { super(clerk); }

    // decorated sell method to sell accessories when a stringed instrument is sold
    public boolean TryTransaction(Customer customer, Item item, boolean buying) {
        boolean result = super.TryTransaction(customer, item, buying);
        // if we just sold a stringed instrument
        if (result && !buying && item instanceof Stringed) {
            // define chances to sell each type
            int chances[] = {10,15,20,30};
            ItemType types[] = { ItemType.GIGBAG, ItemType.PRACTICEAMPS, ItemType.CABLES, ItemType.STRINGS };
            // if the item was electric add 10% to each chance
            if (item.GetComponent(Electric.class) != null ) {
                for (int i = 0; i < chances.length; i++) { chances[i] += 10; }
            }
            // try selling accessories
            for (int i = 0; i < chances.length; i++) {
                Print(super.GetName() + " now wants to sell the customer a " + types[i].name() + " to go with their " + item.name_);
                if (customer instanceof User) {
                    // see if user is interested in item type
                    Print("Would you also like to buy a " + types[i].name() + "? (Y/N)");
                    if (GetBoolFromUser()) {
                        // for cables and strings, allow user to pick how many w/in range
                        int num = 1;
                        if (i == 2) { 
                            Print("How many? (1-2)");
                            num = GetIntFromUser(1,2);
                        } else if (i == 3) { 
                            Print("How many? (1-3)");
                            num = GetIntFromUser(1, 3); 
                        }
                        for (int j = 0; j < num; j++) {
                            // get specific items w/ price, ask user if they want it
                            Item toSell = super.CheckForItem(types[i]);
                            if (toSell != null) {
                                Print("Would you like to buy the " + toSell.name_ + " for $" + toSell.list_price_ + "? (Y/N)");
                                if (GetBoolFromUser()) {
                                    super.Sell(toSell, toSell.list_price_);
                                }
                            } 
                        }
                    } 
                } else {
                    if (GetRandomNum(100) < chances[i]) {
                        // for cables and strings try to sell 2 or 3
                        int num = 1;
                        if (i == 2) { num = GetRandomNum(1,3); }
                        else if (i == 3) { num = GetRandomNum(1, 4); }
                        Print("The customer decides they also want to buy " + num + " " + types[i].name());
                        // sell the extra items
                        for (int j = 0; j < num; j++) {
                            Item toSell = super.CheckForItem(types[i]);
                            if (toSell != null) super.Sell(toSell, toSell.list_price_);
                        }
                    }
                }
            }
        }
        return true;
    }
}