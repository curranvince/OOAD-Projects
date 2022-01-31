import java.util.Vector;

enum ItemType {
    PAPERSCORE,
    CD,
    VINYL,
    CDPLAYER,
    RECORDPLAYER,
    MP3PLAYER,
    GUITAR,
    BASS,
    MANDOLIN,
    FLUTE,
    HARMONICA,
    HATS,
    SHIRTS,
    BANDANAS,
    PRACTICEAMPS,
    CABLES,
    STRINGS
}

public class Store {
    CashRegister register_ = new CashRegister();
    Vector<Item> inventory_ = new Vector<Item>();
    Vector<Item> sold_ = new Vector<Item>();
    Vector<Clerk> clerks_ = new Vector<Clerk>();

    int day_of_week_;
    int current_day_;
    int clerk_id_;
    int total_withdrawn_;

    Store() {
        day_of_week_ = 0;
        total_withdrawn_ = 0;
        current_day_ = 1;

        // store start with 3 of each item
        for (ItemType itemType : ItemType.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        
        clerks_.add(new Clerk("Shaggy", 20));
        clerks_.add(new Clerk("Velma", 5));
    }
    
    int GetClerk() {
        int rando = Utility.GetRandomNum(2);
        if (clerks_.get(rando).days_worked_ < 3) {
            return rando;
        } else {
            if (rando == 0) return 1;
            else return 0;
        }
    }

    int GetOffClerk() {
        if (clerk_id_ == 0) return 1;
        else return 0;
    }

    boolean CheckRegister() {
        System.out.println(clerks_.get(clerk_id_).name_ + " checks the register to find $" + register_.money_ );
        if (register_.money_ >= 75) {
            return true;
        } else {
            return false;
        }
    }

    void GoToBank() {
        System.out.println(clerks_.get(clerk_id_).name_ + " goes to the bank to withdraw $1000 for the register" );
        register_.AddMoney(1000);
    }

    void DoInventory() {
        int total = 0;
        for (Item item : inventory_) {
            total += item.purchase_price_;
        }
        System.out.println(clerks_.get(clerk_id_).name_ + " does inventory to find we have $" + total + " worth of product");
    }

    void RunSimulation() {
        // display inventory
        for (Item item : inventory_) {
            item.Display();
        }
        // each day goes in here
        for (int i = 0; i < 30; i++) {
            // pick whos working
            clerk_id_ = GetClerk();
            // update workers days worked stats
            clerks_.get(GetOffClerk()).ResetDaysWorked();
            clerks_.get(clerk_id_).IncrementDaysWorked();
            // open store
            clerks_.get(clerk_id_).ArriveAtStore(i + 1);
            // check the register
            if (!CheckRegister()) {
                // if not enough in register go to bank
                GoToBank();
            }
            // do inventory
            // order items of type we have none
        }  
        
    };
}
