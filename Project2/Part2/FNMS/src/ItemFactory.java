// Factory to easily create Items
// Implements polymorphism by returning a subclass as the class
// IE we can refer to them all as 'Item' but they will
// take on the behavior of the subclass
public class ItemFactory {
    private ItemFactory() {};
    
    static public Item MakeItem(String itemType) {
        if (itemType == null) {
            return null;
        } else if (itemType.equalsIgnoreCase("PAPERSCORE")) {
            return new PaperScore();
        } else if (itemType.equalsIgnoreCase("CD")) {
            return new CD();
        } else if (itemType.equalsIgnoreCase("VINYL")) {
            return new Vinyl();
        } else if (itemType.equalsIgnoreCase("CDPLAYER")) {
            return new CDPlayer();
        } else if (itemType.equalsIgnoreCase("RECORDPLAYER")) {
            return new RecordPlayer();
        } else if (itemType.equalsIgnoreCase("MP3PLAYER")) {
            return new MP3Player();
        } else if (itemType.equalsIgnoreCase("GUITAR")) {
            return new Guitar();
        } else if (itemType.equalsIgnoreCase("BASS")) {
            return new Bass();
        } else if (itemType.equalsIgnoreCase("MANDOLIN")) {
            return new Mandolin();
        } else if (itemType.equalsIgnoreCase("FLUTE")) {
            return new Flute();
        } else if (itemType.equalsIgnoreCase("HARMONICA")) {
            return new Harmonica();
        } else if (itemType.equalsIgnoreCase("HATS")) {
            return new Hats();
        } else if (itemType.equalsIgnoreCase("SHIRTS")) {
            return new Shirts();
        } else if (itemType.equalsIgnoreCase("BANDANAS")) {
            return new Bandanas();
        } else if (itemType.equalsIgnoreCase("PRACTICEAMPS")) {
            return new PracticeAmps();
        } else if (itemType.equalsIgnoreCase("CABLES")) {
            return new Cables();
        } else if (itemType.equalsIgnoreCase("STRINGS")) {
            return new Strings();
        } 
        return null;
    }
}
