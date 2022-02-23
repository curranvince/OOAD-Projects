package FNMS;

import FNMS.Item.ItemType;

// Customer can be extended to make customers with new types of requests, like trade or fix
abstract class Customer implements Utility {
    // all customers have a name, request, and type of item they're interested in
    protected String name_;
    protected RequestType request_;
    protected Item item_;

    public enum RequestType {
        Buy,
        Sell
    }

    Customer() {
        name_ = CreateName();
        item_ = ItemFactory.MakeItem(GetRandomEnumVal(ItemType.class).name());
    }
    
    public Item GetItem() { return item_; }
    public Item.ItemType GetItemType() { return item_.itemType_; }
    
    public RequestType MakeRequest() { 
        Print(name_ + " has come into the store looking to " + ((request_ == RequestType.Buy) ? "buy" : "sell") + " a " + item_.itemType_.name()); 
        return request_;
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
    public Buyer() {
        super();
        request_ = RequestType.Buy;
    }
}

class Seller extends Customer {
    public Seller() {
        super();
        request_ = RequestType.Sell;
    }
}
