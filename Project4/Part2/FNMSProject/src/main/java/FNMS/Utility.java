package FNMS;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;

interface Utility {
    // make sure theres only one random and scanner instead of instantiating everywhere
    final Random random = new Random();
    final Scanner scanner = new Scanner(System.in);
    final TeeStream teeStream = new TeeStream();

    // TeeStream to write to multiple streams at once idea from
    // https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/output/TeeOutputStream.html
    class TeeStream { 
        private FileOutputStream fileStream;
        private OutputStream outStream = System.out;
        
        public TeeStream() {
            try {
                File file = new File("output/Output.txt");
                file.getParentFile().mkdirs();
                file.createNewFile();
                fileStream = new FileOutputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Write(String msg) {
            msg = (msg + "\n");
            try {
                fileStream.write(msg.getBytes()); // https://www.baeldung.com/java-string-to-byte-array
                outStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Print everything to Sys.out as well as Output.txt
    // For easy interaction as well as capture
    default void Print(String str) { teeStream.Write(str); }
    
    // simple methods for getting random nums
    default int GetRandomNum(int range) { return random.nextInt(range); }
    default int GetRandomNum(int min, int max) { return random.nextInt(max-min) + min; }
    default int GetRandomNumEx(int min, int max, List<Integer> excludes) {
        int rando = random.nextInt(max-min) + min;
        while (excludes.contains(rando)) {
            if (rando < (max-1)) rando++;
            else rando = min;
        }
        return rando;
    }
   
    // get int from user with bounds
    // https://stackoverflow.com/questions/38830142/how-to-handle-invalid-input-when-using-scanner-nextint
    default int GetIntFromUser(int min, int max) {
        int choice = -1; 
        while (choice < min || choice > max) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume eol char
                Print(String.valueOf(choice)); 
                if (choice < min || choice > max) Print("Please input a valid integer");
            } catch (InputMismatchException e) {
                Print("Please input a valid integer");
                scanner.nextLine();
            } 
        }
        return choice;
    }
    
    // get yes or no from user
    default boolean GetBoolFromUser() {
        while (true) {
            String input = scanner.nextLine();
            Print(input);
            if (input.toLowerCase().startsWith("y")) return true;
            else if (input.toLowerCase().startsWith("n")) return false;
            else Print("Please input Y or N");
        }
    }

    // get a random variate coming from a poisson distribution with given mean
    // https://stackoverflow.com/questions/9832919/generate-poisson-arrival-in-java
    // https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
    default int GetPoissonRandom(int mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p*random.nextDouble();
            k++;
        } while (p > L);
        return k-1;
    }

    // taken from profs project 2 code
    // utility for getting a random enum value from any enum
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    default <T extends Enum<?>> T GetRandomEnumVal(Class<T> clazz){
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
    
    // return random price based off condition
    default int GetOfferPrice(Item.Condition condition) {
        int max = (condition.ordinal() * 10) + 1;
        int min = max - 11;
        if (min == 0) min++;
        return (GetRandomNum(min, max));
    }

    default <T> T ChooseFromList(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            Print(String.valueOf(i) + ": " + list.get(i).toString());
        }
        return list.get(GetIntFromUser(0,(list.size()-1)));
    }
}

// interface to make custom names for items
interface ItemUtility extends Utility {
    default String[] GetMusicTitle() {
        String[][] combos = {
            {"Ten", "Pearl Jam"},
            {"The Black Album", "Jay-Z"},
            {"Born to Run", "Bruce Springsteen"},
            {"Blond", "Frank Ocean"},
            {"Paranoid", "Black Sabbath"},
            {"21", "Adele"},
            {"The Wall", "Pink Floyd"},
            {"My Beautiful Dark Twisted Fantasy", "Kanye West"},
            {"Exile on Main Street", "The Rolling Stones"},
            {"Red", "Taylor Swift"},
            {"Master of Puppets", "Metallica"},
            {"Supa Dupa Fly", "Missy Elliott"},
            {"Legend", "Bob Marley and the Wailers"},
            {"Back in Black", "AC/DC"},
            {"Straight Outta Compton", "N.W.A."},
            {"Appetite for Destruction", "Guns N' Roses"},
            {"Songs in the Key of Life", "Stevie Wonder"},
            {"Illmatic", "Nas"},
            {"Blood on the Tracks", "Bob Dylan"},
            {"The Chronic", "Dr. Dre"},
            {"Abbey Road", "The Beatles"},
            {"Back to Black", "Amy Winehouse"},
            {"Lemonade", "Beyonce"},
            {"Are You Experienced", "Jimi Hendrix"},
            {"Enter the Wu-Tang(36 Chambers)", "Wu-Tang Clan"},
            {"Ready to Die", "The Notorious B.I.G."},
            {"To Pimp A ButterFly", "Kendrick Lamar"},
            {"The Miseducation of Lauryn Hill", "Lauryn Hill"},
            {"Purple Rain", "Prince and the Revolution"},
            {"Rumors", "Fleetwood Mac"},
            {"Nevermind", "Nirvana"},
            {"What's Going On", "Marvin Gaye"}
        };
        return combos[GetRandomNum(combos.length)];
    }

    default String GetStringedBrand() {
        String[] brands = {
            "Gibson",
            "Guild",
            "Seagull",
            "Yamaha",
            "Ovation",
            "Washburn",
            "Fender"
        };
        return brands[GetRandomNum(brands.length)];
    }

    default String GetFluteBrand() {
        String[] brands = {
            "Miyazawa",
            "Emerson",
            "Gemeinhardt",
            "Yamaha"
        };
        return brands[GetRandomNum(brands.length)];
    }

    default String GetHarmonicaBrand() {
        String[] brands = {
            "Suzuki",
            "SEYDEL",
            "Lee Oskar",
            "Boseno"
        };
        return brands[GetRandomNum(brands.length)];
    }

    default String GetHarmonicaKey() {
        String[] keys = {
            "C",
            "A",
            "D",
            "F",
            "F#",
            "G"
        };
        return keys[GetRandomNum(keys.length)];
    }

    default String GetClothingBrand() {
        String[] brands = {
            "Nirvna",
            "Pearl Jam",
            "Led Zeppelin",
            "The Beatles",
            "AC/DC",
            "Aerosmith",
            "Metallica",
            "The Notorious B.I.G.",
            "Tupac",
            "Red Hot Chili Peppers",
            "Motley Crue",
            "The Rolling Stones"
        };
        return brands[GetRandomNum(brands.length)];
    }

    default String GetBandanaColor() {
        String[] colors = {
            "Red",
            "Blue",
            "Yellow",
            "Green",
            "Black"
        };
        return colors[GetRandomNum(colors.length)];
    }
    
    default String GetStringsType() {
        String[] types = {
            "Guitar",
            "Violin",
            "Mandolin",
            "Banjo",
            "Bass",
            "Cello"
        };
        return types[GetRandomNum(types.length)];
    }
}