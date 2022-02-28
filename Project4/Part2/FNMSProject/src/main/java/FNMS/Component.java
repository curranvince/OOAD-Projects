package FNMS;

abstract class Component implements Utility {}

class Tuneable extends Component {
    private boolean tuned_;

    Tuneable() { tuned_ = false; }

    void Tune() { tuned_ = true; };
    void Untune() { tuned_ = false; };
    boolean IsTuned() { return tuned_; }
}

class Electric extends Component {}

abstract class KitComponent extends Component {
    protected String name_;
    protected int price_;
    protected String id_; 

    public enum GKComponents {
        Pickups,
        Pickguard,
        Covers,
        KnobSet,
        Bridge,
        Neck
    }

    KitComponent() {
        id_ = String.valueOf(GetRandomChar());
        price_ = GetRandomNum(5,21); 
    }

    public String GetName() { return name_ + " " + id_; }
    public int GetPrice() { return price_; }
    // https://stackoverflow.com/questions/2626835/is-there-functionality-to-generate-a-random-character-in-java
    private char GetRandomChar() { return (char)(random.nextInt(26) + 'a'); }
}

abstract class NorthComponent extends KitComponent {
    NorthComponent() { name_ = "North "; }
}

abstract class SouthComponent extends KitComponent {
    SouthComponent() { name_ = "South "; }
}

class NorthPickups extends NorthComponent {
    NorthPickups() { name_ += "Pickups"; }
}

class SouthPickups extends SouthComponent {
    SouthPickups() { name_ += "Pickups"; }
}

class NorthPickguard extends NorthComponent {
    NorthPickguard() { name_ += "Pickguard"; }
}

class SouthPickguard extends SouthComponent {
    SouthPickguard() { name_ += "Pickguard"; }
}

class NorthCovers extends NorthComponent {
    NorthCovers() { name_ += "Covers"; }
}

class SouthCovers extends SouthComponent {
    SouthCovers() { name_ += "Covers"; }
}

class NorthKnobSet extends NorthComponent {
    NorthKnobSet() { name_ += "Knob Set"; }
}

class SouthKnobSet extends SouthComponent {
    SouthKnobSet() { name_ += "Knob Set"; }
}

class NorthBridge extends NorthComponent {
    NorthBridge() { name_ += "Bridge"; }
}

class SouthBridge extends SouthComponent {
    SouthBridge() { name_ += "Bridge"; }
}

class NorthNeck extends NorthComponent {
    NorthNeck() { name_ += "Neck"; }
}

class SouthNeck extends SouthComponent {
    SouthNeck() { name_ += "Neck"; }
}