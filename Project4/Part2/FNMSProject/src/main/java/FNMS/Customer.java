package FNMS;

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

class User extends Customer {
    private Store store_;

    User(Simulation simulation) {
        name_ = "User";
        request_ = new SwitchRequest(simulation, this);
    }

    public void SetStore(Store store) { store_ = store; }

    public void SetRequest(Request request) {
        request_ = request; 
    }
}