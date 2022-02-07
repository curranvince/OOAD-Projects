public class Customer {
    String name_ = "Alice";
    boolean buying_;
    ItemType item_;

    Customer(boolean buying) {
        buying_ = buying;
        item_ = Utility.GetRandomItemType();
    }

    public void DisplayRequest() { System.out.println(name_ + " has come into the store looking to " + (buying_ ? "buy" : "sell") + " a " + item_.name()); }
}
