public class ItemFactory {
    
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
        } 
        return null;
    }
}
