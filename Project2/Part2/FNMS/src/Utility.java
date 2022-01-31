import java.util.Random;

public class Utility {
    static Random random = new Random();

    static int GetRandomNum(int range) { return random.nextInt(range); }
    static int GetRandomNum(int min, int max) { return random.nextInt(max-min)+min; }

    static Size GetRandomSize() {
        int rando = GetRandomNum(3);
        if (rando == 0) {
            return Size.Small;
        } else if (rando == 1) {
            return Size.Medium;
        } else {
            return Size.Large;
        }
    }

}
