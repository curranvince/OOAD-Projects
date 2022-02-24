package FNMS;

interface KitFactory {
    Pickups CreatePickups();
    Pickguard CreatePickguard();
    Covers CreateCovers();
    KnobSet CreateKnobSet();
    Bridge CreateBridge();
    Neck CreateNeck();
}

class NorthKitFactory implements KitFactory {
    public Pickups CreatePickups() { return new NorthPickups(); };
    public Pickguard CreatePickguard() { return new NorthPickguard(); };
    public Covers CreateCovers() { return new NorthCovers(); };
    public KnobSet CreateKnobSet() { return new NorthKnobSet(); };
    public Bridge CreateBridge() { return new NorthBridge(); };
    public Neck CreateNeck() { return new NorthNeck(); };
}

class SouthKitFactory implements KitFactory {
    public Pickups CreatePickups() { return new SouthPickups(); };
    public Pickguard CreatePickguard() { return new SouthPickguard(); };
    public Covers CreateCovers() { return new SouthCovers(); };
    public KnobSet CreateKnobSet() { return new SouthKnobSet(); };
    public Bridge CreateBridge() { return new SouthBridge(); };
    public Neck CreateNeck() { return new SouthNeck(); };
}