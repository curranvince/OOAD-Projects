abstract class Customer implements Utility {
    protected String name_;
    protected int request_;
    protected Item.ItemType itemtype_;

    public Item.ItemType GetItemType() { return itemtype_; }
    
    public int MakeRequest() { 
        Print(name_ + " has come into the store looking to " + ((request_ == 1) ? "buy" : "sell") + " a " + itemtype_.name()); 
        return request_;
    }

    Customer() {
        name_ = GetRandomName();
        itemtype_ = GetRandomItemType();
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
