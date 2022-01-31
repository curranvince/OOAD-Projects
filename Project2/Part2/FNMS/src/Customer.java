public class Customer {
    String name_ = "Alice";
    boolean buying_;
    ItemType item_;

    Customer(boolean buying) {
        buying_ = buying;
    }

    public void DisplayRequest() {
        if (buying_) {
            System.out.println(name_ + " has come into the store looking to buy a " + item_.name());
        } else {
            System.out.println(name_ + " has come into the store looking to sell a " + item_.name());
        }
    }
}
