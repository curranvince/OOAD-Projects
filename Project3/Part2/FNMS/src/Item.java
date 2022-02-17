// TO DO
// Add new items
//  - create class
//  - add to itemtype enum
//  - add to factory

// Item is an example of an abstraction
// You cannot create an 'Item', but must instead create
// one of its concrete subclasses
abstract class Item implements Utility {
    ItemType itemType;
    String name_;
    int purchase_price_;
    int list_price_;
    int day_arrived;
    String condition_;
    int sale_price_;
    int day_sold_;

    public enum ItemType {
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

    public enum Size {
        Small, 
        Medium, 
        Large
    }

    Item() {
        name_ = "";
        // Items have a purchase price $1-50, and list price is double
        purchase_price_ = GetRandomNum(1, 51);
        list_price_ = 2*purchase_price_;
        day_arrived = 0;
        condition_ = GetRandomCondition();
    };

    void Display() {
        Print(name_ + " for $" + list_price_);
    }

    void DisplaySold() {
        Print(name_ + " for $" + sale_price_ + " on Day " + day_sold_);
    }

    boolean LowerCondition() {
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
                Print(name_ + " has been broken!");
                // return false if item breaks
                condition_ = "Broken";
                return false;
            default:
                Print("ERROR: Item::LowerCondition given bad paramater");
                return true;
        }
        // lower price of item and return true for success
        list_price_ -= (int)list_price_*0.2;
        return true;
    }
    // these methods do nothing unless the item has the required properties
    void Tune() {};
    void Untune() {};
    boolean IsTuned() { return false; }
    boolean NeedsTuning() { return false; }
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

abstract class Players extends Item {
    private boolean equalized_ = false;

    void Tune() { equalized_ = true; }
    void Untune() { equalized_ = false; }
    boolean IsTuned() { return equalized_; }
    boolean NeedsTuning() { return true; }
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

abstract class Instruments extends Item {}

abstract class Stringed extends Instruments { 
    private boolean electric_ = false;
    private boolean tuned_ = false;
    // assuming theres a 50/50 chance any Stringed instrument is electric
    Stringed() {
        name_ += GetStringedBrand() + " ";
        if (GetRandomNum(2) == 1) {
            electric_ = true;
            name_ += "Electric ";
        }
    }

    void Tune() { tuned_ = true; }
    void Untune() { tuned_ = false; }
    boolean IsTuned() { return tuned_; }
    boolean NeedsTuning() { return true; }
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
    private boolean adjusted_ = false;

    void Tune() { adjusted_ = true; }
    void Untune() { adjusted_ = false; }
    boolean IsTuned() { return adjusted_; }
    boolean NeedsTuning() { return true; }
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
        type_ = GetStringType();
        name_ += type_ + " strings";
        itemType = ItemType.STRINGS;
    }
}