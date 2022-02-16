public class Customer implements Utility {
    private String name_;
    private boolean buying_;
    private Item.ItemType itemtype_;

    public Customer(boolean buying) {
        buying_ = buying;
        name_ = GetRandomName();
        itemtype_ = GetRandomItemType();
    }

    public boolean IsBuying() { return buying_; }
    public Item.ItemType GetItemType() { return itemtype_; }
    public void DisplayRequest() { Print(name_ + " has come into the store looking to " + (buying_ ? "buy" : "sell") + " a " + itemtype_.name()); }
}
