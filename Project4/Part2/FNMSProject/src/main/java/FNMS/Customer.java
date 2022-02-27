package FNMS;

import java.util.Scanner;

import FNMS.Item.ItemType;

// Customer can be extended to make customers with new types of requests, like trade or fix
abstract class Customer implements Utility {
    // all customers have a name, request, and type of item they're interested in
    protected String name_;
    protected Request request_;

    Customer() { name_ = CreateName(); }
    
    public String GetName() { return name_; }
    public void MakeRequest() { 
        Print(name_ + " has come into the store looking to " + request_.toString()); 
        request_.Execute();
    }
    
    public void LeaveStore() { Print(name_ + " leaves the store"); }
    
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

class Buyer extends Customer {
    public Buyer(AbstractClerk clerk) {
        super();
        request_ = new BuyRequest(clerk);
    }
}

class Seller extends Customer {
    public Seller(AbstractClerk clerk) {
        super();
        request_ = new SellRequest(clerk);
    }
}

// User needs an inventory
// User needs to be able to decide to buy or not
class User extends Customer {
    private Simulation sim_;
    private Store store_;
    private boolean leaves_;
   
    User(Simulation simulation) {
        leaves_ = false;
        name_ = "User";
        sim_ = simulation;
        request_ = new SwitchRequest(simulation, this);
    }

    public void Leaves() { leaves_ = true; }
    public void SetStore(Store store) { store_ = store; }

    public void MakeRequests() {
        // first ask which store, then keep allowing requests until user wants to leave
        MakeRequest();
        while (!leaves_) {
            ChooseRequest();
            MakeRequest();
        }
    }

    private ItemType ChooseItemType() {
        // present all the itemtypes
        Print("Please choose an ItemType: ");
        for (int i = 0; i < ItemType.values().length; i++) {
            Print(String.valueOf(i) + ": " + ItemType.values()[i]);
        }
        // return the itemtype user chooses
        return ItemType.values()[GetIntFromUser(0, ItemType.values().length-1)];
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
        if (choice == 1) { request_ = new SwitchRequest(sim_, this); }
        else if (choice == 2) { request_ = new NameRequest(store_.GetActiveClerk()); }
        else if (choice == 3) { request_ = new TimeRequest(store_.GetActiveClerk()); }
        else if (choice == 4) { request_ = new SellRequest(store_.GetActiveClerk(), ChooseItemType()); }
        else if (choice == 5) { request_ = new BuyRequest(store_.GetActiveClerk(), ChooseItemType()); }
        else if (choice == 6) { request_ = new BuyKitRequest(store_); }
        else if (choice == 7) { request_ = new LeaveRequest(this); }
    }
}