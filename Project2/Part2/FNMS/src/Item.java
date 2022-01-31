enum Size {
    Small, 
    Medium, 
    Large
}
public class Item {
    String name_;
    int purchase_price_;
    int list_price_;
    int day_arrived;
    int condition_;
    int sale_price_;
    int day_sold_;

    Item() {
        name_ = "";
        purchase_price_ = Utility.GetRandomNum(1, 51);
        list_price_ = 2*purchase_price_;
        day_arrived = 0;
        condition_ = Utility.GetRandomNum(5);
    };

    void Display() {
        System.out.println(name_ + " for $" + list_price_);
    }
    
}

class Music extends Item {
    String band_;
    String album_;

    Music () {
        band_ = "Beatles";
        album_ = "Walking";
        name_ += album_ + " by " + band_;
    }
    
}

class CD extends Music { 
    CD() { name_ += " CD"; }
}

class Vinyl extends Music {
    Vinyl() { name_ += " Vinyl"; }
}
class PaperScore extends Music {
    PaperScore() { name_ += " Paper Score"; }
}

class Players extends Item {}

class CDPlayer extends Players { 
    CDPlayer() { name_ += "CD Player"; }
}

class RecordPlayer extends Players {
    RecordPlayer() { name_ += "Record Player"; }
}
class MP3Player extends Players {
    MP3Player() { name_ += "MP3 Player"; }
}

class Instruments extends Item {}

class Stringed extends Instruments { 
    boolean electric_ = false;
    // assuming theres a 50/50 chance any Stringed instrument is electric
    Stringed() {
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
    }
}

class Bass extends Stringed {
    Bass() {
        name_ += "Bass";
    }
}

class Mandolin extends Stringed {
    Mandolin() {
        name_ += "Mandolin";
    }
}

class Wind extends Instruments {}

class Flute extends Wind {
    String type_;

    Flute() {
        type_= "Wood";
        name_ += type_ + " Flute";
    }
}

class Harmonica extends Wind {
    String key_;

    Harmonica() {
        key_= "F#";
        name_ += "Harmonica in " + key_;
    }
}

class Clothing extends Item {}

class Hats extends Clothing {
    Size size_;

    Hats() {
        size_ = Utility.GetRandomSize();
        name_ += size_.name() + " Hat";
    }
}

class Shirts extends Clothing {
    Size size_;

    Shirts() {
        size_ = Utility.GetRandomSize();
        name_ += size_.name() + " Shirt";
    }
}

class Bandanas extends Clothing {
    Bandanas() {
        name_ += "Bandana";
    }
}

class Accessories extends Item {}

class PracticeAmps extends Accessories {
    int wattage_;

    PracticeAmps() {
        wattage_ = Utility.GetRandomNum(10, 21);
        name_ += wattage_ + " watt amp";
    }
}

class Cables extends Accessories {
    int length_;

    Cables() {
        length_ = Utility.GetRandomNum(1, 7);
        name_ += length_ + " meter cable";
    }
}

class Strings extends Accessories {
    String type_;

    Strings() {
        type_ = "Guitar";
        name_ += type_ + " strings";
    }
}