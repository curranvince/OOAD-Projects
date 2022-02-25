package FNMS;

import FNMS.Item.ItemType;

abstract class Request implements Utility {
    abstract void Execute();
}

class BuyRequest extends Request {
    private AbstractClerk clerk_;
    private ItemType itemType_;

    public BuyRequest(AbstractClerk aclerk) {
        itemType_ = GetRandomEnumVal(ItemType.class);
        clerk_ = aclerk;
    }

    public void Execute() {
        clerk_.TryTransaction(clerk_.CheckForItem(itemType_), false);
    }

    public String toString() { return "buy a " + itemType_.name(); }
}

class SellRequest extends Request {
    private AbstractClerk clerk_;
    private Item item_;

    public SellRequest(AbstractClerk aclerk) {
        item_ = ItemFactory.MakeItem(GetRandomEnumVal(ItemType.class).name());
        clerk_ = aclerk;
    }

    public void Execute() {
        clerk_.TryTransaction(item_, true);
    }

    public String toString() { return "sell a " + item_.name_; }
}

class NameRequest extends Request {
    private AbstractClerk clerk_;

    public NameRequest(AbstractClerk clerk) { clerk_ = clerk; }

    public void Execute() {
        Print("The clerk says their name is " + clerk_.GetName());
    }

    public String toString() { return " find out the clerks name"; }
}

class TimeRequest extends Request {
    private AbstractClerk clerk_;

    public TimeRequest(AbstractClerk clerk) { clerk_ = clerk; }

    public void Execute() {
        Print("The clerk says its " + clerk_.GetTime());
    }

    public String toString() { return " find out the time"; }
}

class SwitchRequest extends Request {
    Simulation simulation_;
    User user_;

    public SwitchRequest(Simulation simulation, User user) {
        simulation_ = simulation;
        user_ = user;
    }

    public void Execute() {
        Print("The customer wants to switch stores");
        user_.SetStore(simulation_.GetStore());
    }

    public String toString() { return " switch stores"; }
}

class BuyKitRequest extends Request {
    public void Execute() {}
}

class LeaveRequest extends Request {
    public void Execute() {}
}