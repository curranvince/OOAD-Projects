package FNMS;

import java.util.*;

import FNMS.Customer.RequestType;
import FNMS.Item.ItemType;

public class Clerk extends AbstractClerk {
    
    public Clerk(String name, int break_percentage, TuneStrategy tune, Store store) {
        name_ = name;
        break_percentage_ = break_percentage;
        tune_strategy_ = tune;
        store_ = store;
    }  
    
    // receive current days orders when arriving to store
    public void ArriveAtStore() { 
        super.ArriveAtStore();
        int orders_received = 0;
        // check if theres any orders for today
        if (store_.orders_.containsKey(Simulation.current_day_)) {
            // receive orders for each type
            for (ItemType type : store_.orders_.get(Simulation.current_day_)) {
                int total = 0;
                // make 3 concrete items for each type ordered
                for (int i = 0; i < 3; i++) {
                    Item toAdd = ItemFactory.MakeItem(type.name());
                    toAdd.day_arrived = Simulation.current_day_;
                    if (store_.register_.TakeMoney(toAdd.purchase_price_)) {
                        store_.inventory_.add(toAdd);
                        total += toAdd.purchase_price_;
                    }
                }
                Print(name_  + " receives an order with 3 " + type.name() + "s, for $" + total);
                orders_received += 3;
            }
            // remove orders from the map
            store_.orders_.remove(Simulation.current_day_);
        } else {
            // if no orders today then broadcast it
            Print(name_  + " finds no orders delivered today"); 
        }
        Publish("itemsadded", orders_received);
    }

    // broadcast register amount and return if its greater than 75 or not
    public boolean CheckRegister() {
        Print(name_ + " checks the register to find $" + store_.register_.GetAmount());
        Publish("checkedregister", store_.register_.GetAmount());
        return (store_.register_.GetAmount() >= 75) ? true : false;
    }

    // add 1000 to register and broadcast
    public void GoToBank() {
        Print(name_ + " goes to the bank to withdraw $1000 for the register" );
        store_.register_.AddMoney(1000);
        store_.total_withdrawn_ += 1000;
        Publish("checkedregister", store_.register_.GetAmount());
    }

    // return how many items of a certain type we have
    private int GetNumItemsByType(ItemType itemType) {
        int num = 0;
        for (Item item : store_.inventory_) {
            if (item.itemType_ == itemType) num++;
        }
        return num;
    }

    // discontinue an item
    private void UpdateDiscontinuedStatus(ItemType itemType) {
        if (GetNumItemsByType(itemType) == 0 && !store_.discontinued_.contains(itemType)) store_.Discontiue(itemType);
    }

    // tune item, print result, and return wether item was damaged or not
    private boolean Tune(Item item) {
        Tuneable tuneable = item.GetComponent(Tuneable.class);
        Print(name_ + " is attempting to tune the " + item.name_);
        switch (tune_strategy_.Execute(item.GetComponent(Tuneable.class))) {
            case -1:
                Print(name_ + " has done a bad job tuning, and untuned the " + item.name_);
                if (GetRandomNum(10) == 0) {
                    Print(name_ + " has done such a bad job tuning they damaged the item");
                    item.LowerCondition();
                }
                return false;
            case 0:
                Print(name_ + " has not changed the state of the " + item.name_ + ", it is still " + (tuneable.IsTuned() ? "tuned" : "untuned"));
                return true;
            case 1:
                Print(name_ + " has successfully tuned the " + item.name_);
                return true;
            default:
                Print("Error: Tune returned bad value");
                return true;
        }
    }

    // go through stores inventory, tuning items as we go
    // return the set of itemtypes which we do not have
    public Set<ItemType> DoInventory() {
        // vector to keep track of what types we need to order (start with all and remove)
        Set<ItemType> orderTypes = new HashSet<ItemType>();  
        Collections.addAll(orderTypes, ItemType.values()); // https://www.geeksforgeeks.org/java-program-to-convert-array-to-vector/      
        int total, totalitems, damaged;
        total = totalitems = damaged = 0;
        Iterator<Item> it = store_.inventory_.iterator(); // https://www.w3schools.com/java/java_iterator.asp#:~:text=An%20Iterator%20is%20an%20object,util%20package.
        while (it.hasNext()) {
            Item item = it.next();
            // remove this type from the list of types we need to order
            if (orderTypes.contains(item.itemType_)) orderTypes.remove(item.itemType_);
            // tune certain items
            if (item.GetComponent(Tuneable.class) != null) {
                if (!Tune(item)) {
                    damaged++;
                    if (item.condition_ == Item.Condition.broken) {it.remove(); }
                }
            }
            // add value of item to total
            total += item.purchase_price_;
            totalitems++;
        }
        // broadcast total value of inventory
        Print(name_ + " does inventory to find we have $" + total + " worth of product");
        // publish amount of items, value, and how many broken in tuning
        Publish("brokeintuning", damaged);
        Publish("totalitems", totalitems);
        Publish("totalitemsprice", total);
        return orderTypes;
    }

    // take in a set of itemtypes we don't have, and return # of items ordered
    public int PlaceOrders(Set<ItemType> orderTypes) {
        int orders = 0;
        // remove discontinued items
        orderTypes.removeAll(store_.discontinued_);
        // remove items weve already ordered
        for (List<ItemType> vec : store_.orders_.values()) { orderTypes.removeAll(vec); }
        // place orders
        if (!orderTypes.isEmpty()) { 
            for (ItemType type : orderTypes) {
                int deliveryDay = GetRandomNum(1, 4) + Simulation.current_day_;
                // make sure orders arent delivered on Sunday
                if (deliveryDay % 7 == 0) { deliveryDay++; }
                // if date isnt in order system then add it
                if (!store_.orders_.containsKey(deliveryDay)) { store_.orders_.put(deliveryDay, new Vector<ItemType>()); } 
                // add order to delivery day
                store_.orders_.get(deliveryDay).add(type);
                // broadcast who placed an order of what and what day it will arrive
                Print(name_ + " placed an order for 3 " + type.name() + "s to arrive on Day " + deliveryDay);
                orders += 3;
            }
        } 
        // print and publish amount of orders placed
        Print(name_ + " placed " + String.valueOf(orders) + " order(s) today");
        Publish("itemsordered", orders);
        return orders;
    }

    // sell an item to a customer
    public int Sell(Item item, int salePrice) {
        Print("The customer buys the " + item.name_ + " for $" + salePrice);
        // add money to register, update item stats, update inventories
        store_.register_.AddMoney(salePrice);
        item.day_sold_ = Simulation.current_day_;
        item.sale_price_ = salePrice;
        store_.inventory_.remove(item);
        store_.sold_.add(item);
        // update discontinued items whenever clothing is sold
        if (item instanceof Clothing) UpdateDiscontinuedStatus(item.itemType_);
        return 1;
    }

    // buy an item from a customer 
    public int Buy(Item item, int salePrice) {
        if (store_.register_.TakeMoney(salePrice)) {
            Print("The store buys the " + item.name_ + " in " + item.condition_ + " condition for $" + salePrice);
            // take money from register, update item stats, update inventories
            item.purchase_price_ = salePrice;
            item.list_price_ = salePrice*2;
            item.day_arrived = Simulation.current_day_;
            store_.inventory_.add(item);
            return 1;
        }  
        Print("Unfortunately, the store doesn't have enough money to buy the " + item.name_);
        return 0;
    }

    // return wether a transaction is accepted
    public boolean OfferAccepted(Item item, boolean buying, boolean discount) {
        int chance = 50;
        if (discount) chance += 25;
        if (!buying) {
            if (item.GetComponent(Tuneable.class) != null && item.GetComponent(Tuneable.class).IsTuned()) {
                chance += 10;
                if (item instanceof Stringed) chance += 5;
                else if (item instanceof Wind) chance += 10;
            }
        }
        return (GetRandomNum(100) < chance);
    }

    // handle buying or selling
    public Pair<RequestType, Integer> TryTransaction(Item item, boolean buying) {
        if (item == null) { return (buying ? new Pair<RequestType, Integer>(RequestType.Sell, 0) : new Pair<RequestType, Integer>(RequestType.Buy, 0)); }
        // get price based off condition, or list price
        int price = buying ? GetOfferPrice(item.condition_) : item.list_price_;
        Print(name_ + (buying ? (" determines the " + item.name_ + " to be in " + item.condition_ + " condition and the value to be $") : (" shows the customer the " + item.name_  + ", selling for $")) + price );
        // if customer will buy at initial price
        if (OfferAccepted(item, buying, false)) {
            // buy or sell the item at price
            return (buying ? new Pair<RequestType, Integer>(RequestType.Sell, Buy(item, price)) : new Pair<RequestType, Integer>(RequestType.Buy, Sell(item, price)));
        } else {
            // offer new price more favorable to customer
            Print(name_ + " offers a 10% " + (buying ? "increase" : "discount") + " to the original price");
            // if customer will buy at changed price
            if (OfferAccepted(item, buying, true)) {
                // buy or sell item at price +/- 10%
                return (buying ? new Pair<RequestType, Integer>(RequestType.Sell, Buy(item, (int)(price+(price*0.1)))) : new Pair<RequestType, Integer>(RequestType.Buy, Sell(item, (int)(price-(price*0.1)))));
            } else {
                // customer does not want to complete transaction after new price
                Print("The customer still does not want to " + (buying ? "sell" : "buy") + " the " + item.name_);
            }
        }
        return (buying ? new Pair<RequestType, Integer>(RequestType.Sell, 0) : new Pair<RequestType, Integer>(RequestType.Buy, 0));
    }

    // look through inventory for an itemtype, return first item found of type
    public Item CheckForItem(ItemType itemType) {
        for (Item item : store_.inventory_) {
            if (item.itemType_ == itemType) {
                Print(name_ + " found a " + item.name_ + " in the inventory");
                return item;
            }
        }
        Print(name_ + " finds no " + itemType.name() + " in the inventory");
        return null;
    }

    // route customers request to appropriate method
    public Pair<RequestType, Integer> HandleCustomer(Customer customer) {
        RequestType request = customer.MakeRequest();
        // check for item being discontinued
        if (store_.discontinued_.size() == 3 && customer.item_ instanceof Clothing) { 
            // if all clothings been discontinued, we no longer buy it from customer or order
            Print(name_ + " tells the customer we're out of all clothing items, so we no longer buy them from customers or order them"); 
            return new Pair<RequestType, Integer>(request, 0);
        } else if (request == RequestType.Buy && store_.discontinued_.contains(customer.item_.itemType_)) {
            Print(name_ + " tells the customer we're out of " + customer.GetItemType() + " and will not order anymore, though we will buy them from customers"); 
            return new Pair<RequestType, Integer>(request, 0);
        }
        return ((request == RequestType.Buy) ? TryTransaction(CheckForItem(customer.GetItemType()), false) : TryTransaction(customer.GetItem(), true));
        /* alternate implementation for adding in more customer requests (ie 'trade')
        switch (customer.DisplayRequest()) {
            case Customer.RequestType.Buy: 
                return TryTransaction(CheckForItem(customer.GetItemType()), false);
            case Customer.RequestType.Sell:
                return TryTransaction(customer.GetItem(), true));
            default:
                Print("ERROR: Clerk.HandleCustomer given bad value")
        }
        */
    }

    public void CleanStore() {
        Print("The store closes for the day and " + name_ + " begins cleaning");
        if (GetRandomNum(100) < break_percentage_) { 
            // pick a random item for the clerk to break
            Item toBreak = store_.inventory_.get(GetRandomNum(store_.inventory_.size()));
            Print("Oh no! " + name_ + " broke a " + toBreak.name_ + " while cleaning");
            // lower condition of item and remove if it fully breaks
            if (!toBreak.LowerCondition()) { store_.inventory_.remove(toBreak); }
            if (toBreak instanceof Clothing) UpdateDiscontinuedStatus(toBreak.itemType_);
            Publish("damagedcleaning", 1);
        } else {
            // nothing breaks
            Print(name_ + " cleans the store without incident");
            Publish("damagedcleaning", 0);
        }
    }
}
