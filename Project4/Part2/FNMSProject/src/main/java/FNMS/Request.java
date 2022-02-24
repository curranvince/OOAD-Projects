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

    public String toString() {
        return "buy a " + itemType_.name();
    }
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

    public String toString() {
        return "sell a " + item_.name_;
    }
}