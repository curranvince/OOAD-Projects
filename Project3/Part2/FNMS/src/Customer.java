public class Customer {
    String name_;
    boolean buying_;
    ItemType item_;

    Customer(boolean buying) {
        buying_ = buying;
        name_ = Utility.GetRandomName();
        item_ = Utility.GetRandomItemType();
    }

    public void DisplayRequest() { System.out.println(name_ + " has come into the store looking to " + (buying_ ? "buy" : "sell") + " a " + item_.name()); }
}
