import java.util.Vector;
// TO DO
// Add new items
//  - create class
//  - add to itemtype enum
//  - add to factory

// Item is an example of an abstraction
// You cannot create an 'Item', but must instead create
// one of its concrete subclasses
abstract class Item implements Utility {
    Vector<Component> components_;
    ItemType itemType;
    String name_;
    int purchase_price_;
    int list_price_;
    int day_arrived;
    String condition_;
    int sale_price_;
    int day_sold_;

    public enum ItemType {
        PAPERSCORE,     // 0 Music
        CD,             // 1
        VINYL,          // 2
        CASSETTE,       // 3
        CDPLAYER,       // 4 Players
        RECORDPLAYER,   // 5  
        MP3PLAYER,      // 6
        CASSETTEPLAYER, // 7
        GUITAR,         // 8  Instruments Stringed
        BASS,           // 9
        MANDOLIN,       // 10
        FLUTE,          // 11 Instruments Wind
        HARMONICA,      // 12
        SAXOPHONE,      // 13
        HATS,           // 14 Clothing
        SHIRTS,         // 15
        BANDANAS,       // 16
        PRACTICEAMPS,   // 17 Accessories 
        CABLES,         // 18
        STRINGS,        // 19
        GIGBAG,         // 20
    }

    public enum Size {
        Small, 
        Medium, 
        Large
    }

    Item() {
        components_ = new Vector<Component>();
        name_ = "";
        // Items have a purchase price $1-50, and list price is double
        purchase_price_ = GetRandomNum(1, 51);
        list_price_ = 2*purchase_price_;
        day_arrived = 0;
        condition_ = GetRandomCondition();
    };

    // Resources that helped with the Add/Get Component methods
    // https://stackoverflow.com/questions/10531513/how-to-identify-object-types-in-java
    // https://stackoverflow.com/questions/24600489/get-the-type-of-generic-t
    // https://stackoverflow.com/questions/14524751/cast-object-to-generic-type-for-returning
    // https://stackoverflow.com/questions/2693180/what-is-unchecked-cast-and-how-do-i-check-it

    // add a component to an item
    void AddComponent(Component component) { components_.add(component); }
    
    // get a component from an item
    // suppress the 'typesafe' warning because the code IS typesafe
    @SuppressWarnings("unchecked")
    <T> T GetComponent(Class<T> type) {
        for (Component c : components_) {
            if (type == c.getClass()) {
                return (T)c;
            }
        }
        return null;
    }

    // display an items name and list price
    void Display() { Print(name_ + " for $" + list_price_); }

    // display the day an item sold and the price
    void DisplaySold() { Print(name_ + " for $" + sale_price_ + " on Day " + day_sold_); }

    // lower the condition of an item, including its list price
    // if the item breaks return false, else return true
    boolean LowerCondition() {
        // lower price of item
        list_price_ -= (int)list_price_*0.2;
        // lower condition of item by one
        switch (condition_) {
            case "Excellent":
                Print(name_ + "'s condition has lowered from Excellent to Very Good");
                condition_ = "Very Good";
                break;
            case "Very Good":
                Print(name_ + "'s condition has lowered from Very Good to Good");
                condition_ = "Good";
                break;
            case "Good":
                Print(name_ + "'s condition has lowered from Good to Fair");
                condition_ = "Fair";
                break;
            case "Fair":
                Print(name_ + "'s condition has lowered from Fair to Poor");
                condition_ = "Poor";
                break;
            case "Poor":
                Print(name_ + " condition has lowered enough to be broken!");
                Print(name_ + " has been removed from inventory");
                condition_ = "Broken";
                // return false if item breaks
                return false;
            default:
                Print("ERROR: Item::LowerCondition given bad paramater");
                break;
        }
        Print(name_ + " list price has lowered to " + list_price_);
        return true;
    }
}



// Example of Iheritance : Music inherits from Item
// All the classes below are also examples of Inheritance
abstract class Music extends Item {
    private String band_;
    private String album_;

    Music () {
        String[] combo = GetMusicTitle();
        album_ = combo[0];
        band_ = combo[1];
        name_ += album_ + " by " + band_;
    }
}

class CD extends Music { 
    CD() { 
        name_ += " CD"; 
        itemType = ItemType.CD;
    }
}

class Vinyl extends Music {
    Vinyl() { 
        name_ += " Vinyl"; 
        itemType = ItemType.VINYL;
    }
}

class PaperScore extends Music {
    PaperScore() { 
        name_ += " Paper Score"; 
        itemType = ItemType.PAPERSCORE;
    }
}

class Cassette extends Music {
    Cassette() {
        name_ += " Cassette";
        itemType = ItemType.CASSETTE;
    }
}

abstract class Players extends Item {
    Players() { AddComponent(new Tuneable()); }
}

class CDPlayer extends Players { 
    CDPlayer() { 
        name_ += "CD Player"; 
        itemType = ItemType.CDPLAYER;
    }

}

class RecordPlayer extends Players {
    RecordPlayer() { 
        name_ += "Record Player";
        itemType = ItemType.RECORDPLAYER;
    }
}

class MP3Player extends Players {
    MP3Player() { 
        name_ += "MP3 Player";
        itemType = ItemType.MP3PLAYER;
    }
}

class CassettePlayer extends Players {
    CassettePlayer() {
        name_ += "Cassette Player";
        itemType = ItemType.CASSETTEPLAYER;
    }
}

abstract class Instruments extends Item {}

abstract class Stringed extends Instruments { 
    // assuming theres a 50/50 chance any Stringed instrument is electric
    Stringed() {
        name_ += GetStringedBrand() + " ";
        AddComponent(new Tuneable());
        if (GetRandomNum(2) == 1) {
            AddComponent(new Electric());
            name_ += "Electric ";
        }
    }
}

class Guitar extends Stringed {
    Guitar() {
        name_ += "Guitar";
        itemType = ItemType.GUITAR;
    }
}

class Bass extends Stringed {
    Bass() {
        name_ += "Bass";
        itemType = ItemType.BASS;
    }
}

class Mandolin extends Stringed {
    Mandolin() {
        name_ += "Mandolin";
        itemType = ItemType.MANDOLIN;
    }
}

abstract class Wind extends Instruments {
    Wind() { AddComponent(new Tuneable()); }
}

class Flute extends Wind {
    private String type_;

    Flute() {
        type_= (GetRandomNum(2) == 0) ? " Silver " : " Wood ";
        name_ += GetFluteBrand() + type_ + "Flute";
        itemType = ItemType.FLUTE;
    }
}

class Harmonica extends Wind {
    private String key_;

    Harmonica() {
        key_= GetHarmonicaKey();
        name_ += GetHarmonicaBrand() + " Harmonica in a key of \"" + key_ + "\"";
        itemType = ItemType.HARMONICA;
    }
}

class Saxophone extends Wind {
    private String type_;

    Saxophone() {
        type_= (GetRandomNum(2) == 0) ? " Soprano " : " Alto ";
        name_ += GetFluteBrand() + type_ + "Saxophone";
        itemType = ItemType.SAXOPHONE;
    }
}

abstract class Clothing extends Item {}

class Hats extends Clothing {
    private Size size_;

    Hats() {
        size_ = GetRandomSize();
        name_ += size_.name() + " " + GetClothingBrand() + " Hat";
        itemType = ItemType.HATS;
    }
}

class Shirts extends Clothing {
    private Size size_;

    Shirts() {
        size_ = GetRandomSize();
        name_ += size_.name() + " " + GetClothingBrand() + " Shirt";
        itemType = ItemType.SHIRTS;
    }
}

class Bandanas extends Clothing {
    Bandanas() {
        name_ +=  GetBandanaColor() + " Bandana";
        itemType = ItemType.BANDANAS;
    }
}

abstract class Accessories extends Item {}

class PracticeAmps extends Accessories {
    private int wattage_;

    PracticeAmps() {
        wattage_ = GetRandomNum(10, 21);
        name_ += wattage_ + " watt amp";
        itemType = ItemType.PRACTICEAMPS;
    }
}

class Cables extends Accessories {
    private int length_;

    Cables() {
        length_ = GetRandomNum(1, 7);
        name_ += length_ + " meter cable";
        itemType = ItemType.CABLES;
    }
}

class Strings extends Accessories {
    private String type_;

    Strings() {
        type_ = GetStringsType();
        name_ += type_ + " strings";
        itemType = ItemType.STRINGS;
    }
}

class GigBag extends Accessories {
    GigBag() {
        name_ += "Gig Bag";
        itemType = ItemType.GIGBAG;
    }
}