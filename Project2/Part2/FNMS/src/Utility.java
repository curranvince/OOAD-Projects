import java.util.Random;

public class Utility {
    static Random random = new Random();

    static int GetRandomNum(int range) { return random.nextInt(range); }
    static int GetRandomNum(int min, int max) { return random.nextInt(max-min)+min; }

    static Size GetRandomSize() { return Size.values()[GetRandomNum(3)]; }
    static ItemType GetRandomItemType() { return ItemType.values()[GetRandomNum(17)]; }
    
}
