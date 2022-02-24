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
    String name_;
    int price_;

    KitComponent() { price_ = GetRandomNum(5,21); }
}

abstract class Pickups extends KitComponent {
    Pickups() { name_ += "Pickups "; }
}

class NorthPickups extends Pickups {
    NorthPickups() { name_ += "North "; }
}

class SouthPickups extends Pickups {
    SouthPickups() { name_ += "South "; }
}

abstract class Pickguard extends KitComponent {
    Pickguard() { name_ += "Pickguard "; }
}

class NorthPickguard extends Pickguard {
    NorthPickguard() { name_ += "North "; }
}

class SouthPickguard extends Pickguard {
    SouthPickguard() { name_ += "South"; }
}

abstract class Covers extends KitComponent {
    Covers() { name_ += "Covers "; }
}

class NorthCovers extends Covers {
    NorthCovers() { name_ += "North "; }
}

class SouthCovers extends Covers {
    SouthCovers() { name_ += "South"; }
}

abstract class KnobSet extends KitComponent {
    KnobSet() { name_ += "KnobSet "; }
}

class NorthKnobSet extends KnobSet {
    NorthKnobSet() { name_ += "North "; }
}

class SouthKnobSet extends KnobSet {
    SouthKnobSet() { name_ += "South"; }
}

abstract class Bridge extends KitComponent {
    Bridge() { name_ += "Bridge "; }
}

class NorthBridge extends Bridge {
    NorthBridge() { name_ += "North "; }
}

class SouthBridge extends Bridge {
    SouthBridge() { name_ += "South"; }
}

abstract class Neck extends KitComponent {
    Neck() { name_ += "Neck "; }
}

class NorthNeck extends Neck {
    NorthNeck() { name_ += "North "; }
}

class SouthNeck extends Neck {
    SouthNeck() { name_ += "South"; }
}