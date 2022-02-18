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
    public int HandleCustomer(Customer customer) { return decoratedStaff_.HandleCustomer(customer); }
    public Set<Item.ItemType> DoInventory() { return decoratedStaff_.DoInventory(); }
    public int PlaceOrders(Set<Item.ItemType> orderTypes) { return decoratedStaff_.PlaceOrders(orderTypes); }
    public void CleanStore() { decoratedStaff_.CleanStore(); }
    public void CloseStore() { decoratedStaff_.CloseStore(); }
}

// concrete class to implement new methods/overwrites
class ClerkSellDecorator extends ClerkDecorator {
    public ClerkSellDecorator(AbstractClerk clerk) { 
        super(clerk); 
    }
    
    public int HandleCustomer(Customer customer) {
        int result = super.HandleCustomer(customer);
        if (result < 0) {
            if (customer.GetItemType() == Item.ItemType.GUITAR) {
// TO DO
// DECORATE SELL METHOD
            }
        }
        return result;
    }
}