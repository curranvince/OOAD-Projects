// Customer can be extended to make customers with new types of requests, like trade or fix
abstract class Customer implements Utility {
    // all customers have a name, request, and type of item they're interested in
    protected String name_;
    protected int request_;
    protected Item.ItemType itemtype_;

    Customer() {
        name_ = CreateName();
        itemtype_ = GetRandomItemType();
    }
    
    public Item.ItemType GetItemType() { return itemtype_; }
    
    public int MakeRequest() { 
        Print(name_ + " has come into the store looking to " + ((request_ == 1) ? "buy" : "sell") + " a " + itemtype_.name()); 
        return request_;
    }

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
    public Buyer() {
        super();
        request_ = 1;
    }
}

class Seller extends Customer {
    public Seller() {
        super();
        request_ = -1;
    }
}
