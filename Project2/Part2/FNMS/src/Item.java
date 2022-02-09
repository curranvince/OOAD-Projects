enum Size {
    Small, 
    Medium, 
    Large
}

// Item is an example of an abstraction
// You cannot create an 'Item', but must instead create
// one of its concrete subclasses
abstract class Item {
    ItemType itemType;
    String name_;
    int purchase_price_;
    int list_price_;
    int day_arrived;
    String condition_;
    int sale_price_;
    int day_sold_;

    Item() {
        name_ = "";
        // Items have a purchase price $1-50, and list price is double
        purchase_price_ = Utility.GetRandomNum(1, 51);
        list_price_ = 2*purchase_price_;
        day_arrived = 0;
        condition_ = Utility.GetRandomCondition();
    };

    void Display() {
        System.out.println(name_ + " for $" + list_price_);
    }

    void DisplaySold() {
        System.out.println(name_ + " for $" + sale_price_ + " on Day " + day_sold_);
    }

    boolean LowerCondition() {
        // lower condition of item by one
        switch (condition_) {
            case "Excellent":
                condition_ = "Very Good";
                break;
            case "Very Good":
                condition_ = "Good";
                break;
            case "Good":
                condition_ = "Fair";
                break;
            case "Fair":
                condition_ = "Poor";
                break;
            case "Poor":
                // return false if item breaks
                return false;
            default:
                System.out.println("ERROR: Item::LowerCondition given bad paramater");
                return true;
        }
        // lower price of item and return true for success
        list_price_ -= (int)list_price_*0.2;
        return true;
    }
    
}

// Example of Iheritance : Music inherits from Item
// All the classes below are also examples of Inheritance
abstract class Music extends Item {
    String band_;
    String album_;

    Music () {
        String[] combo = Utility.GetMusicTitle();
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

abstract class Players extends Item {}

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
    boolean electric_ = false;
    // assuming theres a 50/50 chance any Stringed instrument is electric
    Stringed() {
        name_ += Utility.GetStringedBrand() + " ";
        int switcher = Utility.GetRandomNum(2);
        if (switcher == 1) {
            electric_ = true;
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

abstract class Wind extends Instruments {}

class Flute extends Wind {
    String type_;

    Flute() {
        type_= (Utility.GetRandomNum(2) == 0) ? " Silver " : " Wood ";
        name_ += Utility.GetFluteBrand() + type_ + "Flute";
        itemType = ItemType.FLUTE;
    }
}

class Harmonica extends Wind {
    String key_;

    Harmonica() {
        key_= Utility.GetHarmonicaKey();
        name_ += Utility.GetHarmonicaBrand() + " Harmonica in a key of \"" + key_ + "\"";
        itemType = ItemType.HARMONICA;
    }
}

abstract class Clothing extends Item {}

class Hats extends Clothing {
    Size size_;

    Hats() {
        size_ = Utility.GetRandomSize();
        name_ += size_.name() + " " + Utility.GetClothingBrand() + " Hat";
        itemType = ItemType.HATS;
    }
}

class Shirts extends Clothing {
    Size size_;

    Shirts() {
        size_ = Utility.GetRandomSize();
        name_ += size_.name() + " " + Utility.GetClothingBrand() + " Shirt";
        itemType = ItemType.SHIRTS;
    }
}

class Bandanas extends Clothing {
    Bandanas() {
        name_ +=  Utility.GetBandanaColor() + " Bandana";
        itemType = ItemType.BANDANAS;
    }
}

abstract class Accessories extends Item {}

class PracticeAmps extends Accessories {
    int wattage_;

    PracticeAmps() {
        wattage_ = Utility.GetRandomNum(10, 21);
        name_ += wattage_ + " watt amp";
        itemType = ItemType.PRACTICEAMPS;
    }
}

class Cables extends Accessories {
    int length_;

    Cables() {
        length_ = Utility.GetRandomNum(1, 7);
        name_ += length_ + " meter cable";
        itemType = ItemType.CABLES;
    }
}

class Strings extends Accessories {
    String type_;

    Strings() {
        type_ = Utility.GetStringType();
        name_ += type_ + " strings";
        itemType = ItemType.STRINGS;
    }
}