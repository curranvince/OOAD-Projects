import java.util.Vector;

enum ItemTypes {
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

    int day_of_week_;
    int days_completed_;
    
    Store() {
        day_of_week_ = 0;
        days_completed_ = 0;
    }
    
    void RunSimulation() {
        // make 3 of each item
        for (ItemTypes itemType : ItemTypes.values()) {
            for (int i = 0; i < 3; i++) {
                inventory_.add(ItemFactory.MakeItem(itemType.name()));
            }
        }
        // display inventory
        for (Item item : inventory_) {
            item.Display();
        }
        
        // test register
        register_.AddMoney(20);
        register_.Display();

        register_.TakeMoney(10);
        register_.Display();
        
        if (register_.HasEnough(11)) {
            register_.TakeMoney(11);
            register_.Display();
        } else {
            System.out.println("Sorry not enough $");
            register_.Display();
        }
        
    };
}
