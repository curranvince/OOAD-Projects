import java.util.Random;

public class Item {
    String name_;
    int purchase_price_;
    int list_price_;
    boolean newOrUsed;
    int dayArrived;
    int condition_;
    int sale_price_;
    int day_sold_;

    Item() {
        name_ = "";
        list_price_ = 20;
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
        Random rand = new Random();
        int switcher = rand.nextInt(2);
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