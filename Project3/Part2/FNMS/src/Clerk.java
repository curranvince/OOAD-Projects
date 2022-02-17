//TO DO
// Stop selling clothing (and buying from customers)
// Clerk may be sick
// Decorate sell method
import java.util.*;
import java.io.*;
// The Tune interface and its subclasses is an example of the Strategy pattern. 
interface Tune extends Utility { public int Tune(Item item); }

class ManualTune implements Tune {
    // 80% chance to tune, 20% chance to untune
    public int Tune(Item item) { 
        if (!item.IsTuned()) {
            if (GetRandomNum(10) > 1) {
                item.Tune();
                return 1;
            }
        } else {
            if (GetRandomNum(10) > 7) {
                item.Untune();
                return -1;
            }
        }
        return 0;
    }
}

class HaphazardTune implements Tune {
    // 50% chance to flip tune
    public int Tune(Item item) { 
        if (GetRandomNum(2) == 0) {
            if (item.IsTuned()) {
                item.Untune();
                return -1;
            } else {
                item.Tune();
                return 1;
            }
        }
        return 0;
    }
}

class ElectronicTune implements Tune {
    // automatically tune
    public int Tune(Item item) { 
        if (!item.IsTuned()) {
            item.Tune();
            return 1;
        }
        return 0;
    }
}

public class Clerk extends Staff {
    // This is an example of Encapsulation
    // Only the clerk has info about their break percentage
    // and can 'do' things with  it
    private int break_percentage_;
    private Tune tune_;
    
    public Clerk(String name, int break_percentage, Tune tune, Store store) {
        name_ = name;
        break_percentage_ = break_percentage;
        tune_ = tune;
        store_ = store;
    }    
    
    public void ArriveAtStore() { 
        super.ArriveAtStore();
        int orders_received = 0;
        // check if theres any orders for today
        if (store_.orders_.containsKey(Simulation.current_day_)) {
            // receive orders for each type
            for (Item.ItemType type : store_.orders_.get(Simulation.current_day_)) {
                Print(name_  + " finds an order with 3 " + type.name() + "s");
                for (int i = 0; i < 3; i++) {
                    Item toAdd = ItemFactory.MakeItem(type.name());
                    toAdd.day_arrived = Simulation.current_day_;
                    store_.inventory_.add(toAdd);
                    store_.register_.TakeMoney(store_.inventory_.lastElement().purchase_price_);
                    orders_received++;
                }
            }
            // remove orders from the map
            store_.orders_.remove(Simulation.current_day_);
        } else {
            // if no orders today then broadcast it
            Print(name_  + " finds no orders delivered today"); 
        }
        Publish("itemsadded", orders_received);
    }

    public boolean CheckRegister() {
        Publish("checkedregister", store_.register_.GetAmount());
        // broadcast register amount and return if its greater than 75 or not
        Print(name_ + " checks the register to find $" + store_.register_.GetAmount());
        return (store_.register_.GetAmount() >= 75) ? true : false;
    }

    public void GoToBank() {
        // add 1000 to register and broadcast
        Print(name_ + " goes to the bank to withdraw $1000 for the register" );
        store_.register_.AddMoney(1000);
        store_.total_withdrawn_ += 1000;
        Publish("checkedregister", store_.register_.GetAmount());
    }

    public Item CheckForItem(Item.ItemType itemType) {
        for (Item item : store_.inventory_) {
            if (item.itemType == itemType) return item;
        }
        return null;
    }

    // sell an item to a customer
    private void Sell(Item item, int salePrice) {
        Print("The customer buys the " + item.name_ + " for $" + salePrice);
        // add money to register, update item stats, update inventories
        store_.register_.AddMoney(salePrice);
        item.day_sold_ = Simulation.current_day_;
        item.sale_price_ = salePrice;
        store_.inventory_.remove(item);
        store_.sold_.add(item);
    }

    // buy an item from a customer
    private void Buy(Item item, int salePrice) {
        Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + salePrice);
        // take money from register, update item stats, update inventories
        store_.register_.TakeMoney(salePrice);
        item.purchase_price_ = salePrice;
        item.list_price_ = salePrice*2;
        item.day_arrived = Simulation.current_day_;
        store_.inventory_.add(item);
    }

    public boolean TryToSell(Item item) {
        boolean sold = false;
        int willBuy = GetRandomNum(2);
        Print(name_ + " shows the customer the " + item.name_  + ", selling for $" + item.list_price_);
        if (willBuy == 0) {
            // sell item to customer at list price
            Sell(item, item.list_price_);
            sold = true;
        } else {
            // if they dont buy, offer discount to get 75% chance of buying
            Print(name_ + " offers a 10% discount");
            willBuy = GetRandomNum(4);
            if (willBuy != 0) {
                // sell item to customer at discounted price
                Sell(item, (int)(item.list_price_-(item.list_price_*0.1)));
                sold = true;
            } else {
                Print("The customer decides not to buy the " + item.name_);
            }
        }
        return sold;
    }
    
    public boolean TryToBuy(Item item) {
// TO DO
// Check if we still sell the item, if we dont: tell customer
// we dont want the item and do not buy it from them
        boolean bought = false;
        int offerPrice = GetOfferPrice(item.condition_);
        Print(name_ + " determines the " + item.name_ + " to be in " + item.condition_ + " condition and the value to be $" + offerPrice );
        // if we have enough $, offer to buy the item
        if (store_.register_.HasEnough(offerPrice)) {
            int willSell = GetRandomNum(2);
            if (willSell == 0) {
                // buy item at initial offer price
                Buy(item, offerPrice);
                bought = true;
            } else {
                // if customer disagrees, offer 10% increase to price and try again
                Print(name_ + " offers a 10% increase to the price");
                willSell = GetRandomNum(4);
                if (willSell != 0) {
                    // store buys item at 10% extra price
                    Buy(item, (int)(offerPrice+(offerPrice*0.1)));
                    bought = true;
                } else {
                    // if the customer disagrees again let them leave
                    Print("The customer leaves without selling their " + item.name_);
                }
            }
        } else {
            // if we dont have enough money broadcast that
            Print("The store doesn't have enough money to buy the " + item.name_);
        }
        return bought;
    }

    private boolean Tune(Item item) { 
        Print(name_ + " is attempting to tune the " + item.name_);
        switch (tune_.Tune(item)) {
            case -1:
                Print(name_ + " has done a bad job tuning, and untuned the " + item.name_);
                if (GetRandomNum(10) == 0) {
                    Print(name_ + " has done such a bad job tuning they damaged the item");
                    if (item.LowerCondition() == false) item = null;
                }
                return false;
            case 0:
                Print(name_ + " has not changed the state of the " + item.name_ + ", it is still " + (item.IsTuned() ? "tuned" : "untuned"));
                return true;
            case 1:
                Print(name_ + " has successfully tuned the " + item.name_);
                return true;
            default:
                Print("Error: Tune returned bad value");
                return true;
        }
    }

    public Set<Item.ItemType> DoInventory() {
        // vector to keep track of what types we need to order (start with all and remove)
        Set<Item.ItemType> orderTypes = new HashSet<Item.ItemType>();
        Collections.addAll(orderTypes, Item.ItemType.values()); // https://www.geeksforgeeks.org/java-program-to-convert-array-to-vector/
        int total, totalitems, damaged, orders;
        total = totalitems = damaged = orders = 0;
        Iterator<Item> it = store_.inventory_.iterator(); // https://www.w3schools.com/java/java_iterator.asp#:~:text=An%20Iterator%20is%20an%20object,util%20package.
        while (it.hasNext()) {
            Item item = it.next();
            // remove this type from the list of types we need to order
            if (orderTypes.contains(item.itemType)) orderTypes.remove(item.itemType);
            // tune certain items
            if (item.NeedsTuning()) {
                if (!Tune(item)) {
                    damaged++;
                    if (item.condition_ == "Broken") { it.remove(); }
                    else { 
                        total += item.purchase_price_;
                        totalitems++;
                    }
                } 
            } else {
                // add value of item to total
                total += item.purchase_price_;
                totalitems++;
            }
        }
        // broadcast total value of inventory
        Publish("brokeintuning", damaged);
        Print(name_ + " does inventory to find we have $" + total + " worth of product");
        Publish("totalitems", totalitems);
        Publish("totalitemsprice", total);
        return orderTypes;
    }

    public int PlaceOrders(Set<Item.ItemType> orderTypes) {
        int orders = 0;
        if (!orderTypes.isEmpty()) {
            // remove items weve already ordered
            for (Vector<Item.ItemType> vec : store_.orders_.values()) {
                for (Item.ItemType orderedItem : vec) { orderTypes.remove(orderedItem); }
                if (orderTypes.isEmpty()) break;
            }
            // place orders
            if (!orderTypes.isEmpty()) {
                for (Item.ItemType type : orderTypes) {
        //TO DO 
        // stop ordering items we no longer sell
                    int deliveryDay = GetRandomNum(1, 4) + Simulation.current_day_;
                    // make sure orders arent delivered on Sunday
                    if (deliveryDay % 7 == 0) { deliveryDay++; }
                    // if date isnt in order system then add it
                    if (!store_.orders_.containsKey(deliveryDay)) { store_.orders_.put(deliveryDay, new Vector<Item.ItemType>()); } 
                    // add order to delivery day
                    store_.orders_.get(deliveryDay).add(type);
                    // broadcast who placed an order of what and what day it will arrive
                    Print(name_ + " placed an order for 3 " + type.name() + "s to arrive on Day " + deliveryDay);
                    orders += 3;
                }
            } else {
                Print(name_ + " places no orders today");
            }
        } else {
            Print(name_ + " places no orders today");
        }
        Publish("itemsordered", orders);
        return orders;
    }

    public void CleanStore() {
        Print("The store closes for the day and " + name_ + " begins cleaning");
        if (GetRandomNum(100) < break_percentage_) { 
            // pick a random item for the clerk to break
            int breakIndex = GetRandomNum(store_.inventory_.size());
            Item toBreak = store_.inventory_.get(breakIndex);
            Print("Oh no! " + name_ + " broke a " + toBreak.name_ + " while cleaning");
            if (!toBreak.LowerCondition()) {
                // remove items with poor condition
                Print("It was already in poor condition, so it has been removed from inventory");
                store_.inventory_.remove(breakIndex);
            } else {
                // lower condition and list price of non-poor quality items
                Print("It's condition has worsened to " + toBreak.condition_ + ", and its list price has lowered to $" + toBreak.list_price_ );
            }
            Publish("damagedcleaning", 1);
        } else {
            // nothing breaks
            Print(name_ + " cleans the store without incident");
            Publish("damagedcleaning", 0);
        }
    }
}
