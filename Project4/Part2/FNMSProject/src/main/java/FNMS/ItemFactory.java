package FNMS;

// Factory to easily create Items && hide their creation
// Implements polymorphism by returning a subclass as the class
// IE we can refer to them all as 'Item' but they will
// take on the behavior of the subclass
public class ItemFactory {
    private ItemFactory() {};
    
    static public Item MakeItem(String itemType) {
        if (itemType.equalsIgnoreCase("PAPERSCORE")) return new PaperScore();
        else if (itemType.equalsIgnoreCase("CD")) return new CD();
        else if (itemType.equalsIgnoreCase("VINYL")) return new Vinyl();
        else if (itemType.equalsIgnoreCase("CASSETTE")) return new Cassette();
        else if (itemType.equalsIgnoreCase("CDPLAYER")) return new CDPlayer();
        else if (itemType.equalsIgnoreCase("RECORDPLAYER")) return new RecordPlayer();
        else if (itemType.equalsIgnoreCase("MP3PLAYER")) return new MP3Player();
        else if (itemType.equalsIgnoreCase("CASSETTEPLAYER")) return new CassettePlayer();
        else if (itemType.equalsIgnoreCase("GUITAR")) return new Guitar();
        else if (itemType.equalsIgnoreCase("BASS")) return new Bass();
        else if (itemType.equalsIgnoreCase("MANDOLIN")) return new Mandolin();
        else if (itemType.equalsIgnoreCase("FLUTE")) return new Flute();
        else if (itemType.equalsIgnoreCase("HARMONICA")) return new Harmonica();
        else if (itemType.equalsIgnoreCase("SAXOPHONE")) return new Saxophone();
        else if (itemType.equalsIgnoreCase("HATS")) return new Hats();
        else if (itemType.equalsIgnoreCase("SHIRTS")) return new Shirts();
        else if (itemType.equalsIgnoreCase("BANDANAS")) return new Bandanas();
        else if (itemType.equalsIgnoreCase("PRACTICEAMPS")) return new PracticeAmps();
        else if (itemType.equalsIgnoreCase("CABLES")) return new Cables();
        else if (itemType.equalsIgnoreCase("STRINGS")) return new Strings();
        else if (itemType.equalsIgnoreCase("GIGBAG")) return new GigBag();  
        System.out.println("ERROR: ItemFactory given bad value : \"" + itemType + "\" : Item not instantiated");
        return null;
    }
}

// Abstract factory pattern to create items from different groups of related components
interface KitFactory {
    abstract KitComponent CreateComponent(String component);
}

class NorthKitFactory implements KitFactory {
    public KitComponent CreateComponent(String component) {
        if (component.equalsIgnoreCase("PICKUPS")) return new NorthPickups();
        else if (component.equalsIgnoreCase("PICKGUARD")) return new NorthPickguard();
        else if (component.equalsIgnoreCase("COVERS")) return new NorthCovers();
        else if (component.equalsIgnoreCase("KNOBSET")) return new NorthKnobSet();
        else if (component.equalsIgnoreCase("BRIDGE")) return new NorthBridge();
        else if (component.equalsIgnoreCase("NECK")) return new NorthNeck();
        return null;
    }
}

class SouthKitFactory implements KitFactory {
    public KitComponent CreateComponent(String component) {
        if (component.equalsIgnoreCase("PICKUPS")) return new SouthPickups();
        else if (component.equalsIgnoreCase("PICKGUARD")) return new SouthPickguard();
        else if (component.equalsIgnoreCase("COVERS")) return new SouthCovers();
        else if (component.equalsIgnoreCase("KNOBSET")) return new SouthKnobSet();
        else if (component.equalsIgnoreCase("BRIDGE")) return new SouthBridge();
        else if (component.equalsIgnoreCase("NECK")) return new SouthNeck();
        return null;
    }
}