package FNMS;

import java.util.*;

import FNMS.Item.ItemType;

// Customer can be extended to make customers with new types of requests, like trade or fix
abstract class Customer implements Utility {
    // all customers have a name, request, and type of item they're interested in
    protected String name_;
    protected Request request_;

    // all customers are assigned a random name
    Customer() { name_ = CreateName(); }
    
    public String GetName() { return name_; }
    
    // execute the customers request
    public void MakeRequest() { 
        Print(name_ + " has come into the store looking to " + request_.toString()); 
        request_.Execute();
    }
    
    // default behavior to determine if a customer will accept an offer
    public boolean AcceptsOffer(Item item, boolean buying, boolean discount) {
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

    // all customers can leave the store
    public void LeaveStore() { Print(name_ + " leaves the store"); }
    
    // list of random names to choose from
    private String CreateName() {
        String[] names = {
            "Alice",
            "Amber",
            "Emma",
            "Sam",
            "Ryan",
            "Nate",
            "CJ",
            "Bruce",
            "Dom",
            "Delaney",
            "Sophie",
            "Paige",
            "Mel"
        };
        return names[GetRandomNum(names.length)];
    }
}

// buyers use BuyRequest
class Buyer extends Customer {
    public Buyer(AbstractClerk clerk) {
        super();
        request_ = new BuyRequest(clerk, this);
    }
}

// sellers use SellRequest
class Seller extends Customer {
    public Seller(AbstractClerk clerk) {
        super();
        request_ = new SellRequest(clerk, this);
    }
}

class User extends Customer {
    private Simulation sim_; // needed to switch between stores
    private Store store_;    // current store
    private boolean leaves_; // if users ready to leave
    private List<Item> inventory_ = new ArrayList<Item>(); // inventory so we can buy/sell between the two stores
   
    User(Simulation simulation) {
        leaves_ = false;
        sim_ = simulation;
        name_ = "User";     // call user "User"
        // users first request is always to pick a store
        request_ = new SwitchRequest(simulation, this);
    }

    // handle 'where' user is and if they're ready to leave
    public void Leaves() { leaves_ = true; }
    public void SetStore(Store store) { store_ = store; }
    // handling users inventory
    public void RemoveFromInventory(Item item) { inventory_.remove(item); }
    public void AddToInventory(Item item) { inventory_.add(item); }

    @Override
    public boolean AcceptsOffer(Item item, boolean buying, boolean discount) {
        // override accpet offer to allow user to choose
        Print("Would you like to accept this offer? (Y/N)");
        return GetBoolFromUser();
    }

    //handles a users full time in the sim
    public void MakeRequests() {
        // first ask which store, then keep allowing requests until user wants to leave
        MakeRequest();
        while (!leaves_) {
            ChooseRequest();
            MakeRequest();
        }
    }

    // allow user to choose their next request
    private void ChooseRequest() {
        // display requests
        Print("Please choose a request to make: ");
        Print("1: Choose Store");
        Print("2: Ask the clerk their name");
        Print("3: Ask the clerk the time");
        Print("4: Sell an item to the store");
        Print("5: Buy an item from the store");
        Print("6: Buy a custom guitar kit");
        Print("7: End the interaction");
        // get choice and assign request based off it 
        int choice = GetIntFromUser(1, 7); 
        if (choice == 1) { request_ = new SwitchRequest(sim_, this); } // user wants to switch stores
        else if (choice == 2) { request_ = new NameRequest(store_.GetActiveClerk()); } // user wants to know the clerks name
        else if (choice == 3) { request_ = new TimeRequest(store_.GetActiveClerk()); } // user wants to know the time
        else if (choice == 4) { // user wants to sell an item
            // if nothing in inventory allow user to choose an itemtype to add to it
            if (inventory_.size() == 0) {
                Print("You have nothing in your inventory, please choose an ItemType to add");
                inventory_.add(ItemFactory.MakeItem(ChooseFromList(Arrays.asList(ItemType.values())).name()));
            }
            Print("Please choose an item from your inventory to sell");
            request_ = new SellRequest(store_.GetActiveClerk(), this, ChooseFromList(inventory_));
        }
        else if (choice == 5) {  // user wants to buy an item
            Print("Please choose which type of item you would like to buy");
            request_ = new BuyRequest(store_.GetActiveClerk(), this, ChooseFromList(Arrays.asList(ItemType.values())));
        }
        else if (choice == 6) { request_ = new BuyKitRequest(store_, this); }
        else if (choice == 7) { request_ = new LeaveRequest(this); }
    }
}