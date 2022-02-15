public class Customer implements Utility {
    String name_;
    boolean buying_;
    ItemType item_;

    Customer(boolean buying) {
        buying_ = buying;
        name_ = GetRandomName();
        item_ = GetRandomItemType();
    }

    public void DisplayRequest() { Print(name_ + " has come into the store looking to " + (buying_ ? "buy" : "sell") + " a " + item_.name()); }
}
