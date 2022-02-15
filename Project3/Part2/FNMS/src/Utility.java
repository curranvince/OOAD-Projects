import java.util.*;
import java.util.Random;

public interface Utility {
    Random random = new Random();

    default void Print(String str) { System.out.println(str); }

    default int GetRandomNum(int range) { return random.nextInt(range); }
    default int GetRandomNum(int min, int max) { return random.nextInt(max-min) + min; }
    default int GetRandomNumEx(int min, int max, int exclude) {
        int rando = random.nextInt(max-min) + min;
        while (rando == exclude) {
            if (rando < max) rando++;
            else rando = min;
        }
        return rando;
    }

    default Size GetRandomSize() { return Size.values()[GetRandomNum(3)]; }
    default ItemType GetRandomItemType() { return ItemType.values()[GetRandomNum(17)]; }
    
    default Vector<Customer> MakeCustomers() {
        // make vector to return
        Vector<Customer> toServe = new Vector<Customer>();
        // get random amounts of buyers and sellers in range
        int buyers = GetRandomNum(4, 11);
        int sellers = GetRandomNum(1, 5);
        // create buyers and sellers
        for (int i = 0; i < buyers; i++) {
            toServe.add(new Customer(true));
        }
        for (int i = 0; i < sellers; i++) {
            toServe.add(new Customer(false));
        }
        // shuffle vector so we get customers in random order
        Collections.shuffle(toServe);
        return toServe;
    }
    
    default String GetRandomCondition() {
        // return a random 'Condition' for an item
        int rando = GetRandomNum(5);
        switch (rando) {
            case 0:
                return "Poor";
            case 1:
                return "Fair";
            case 2:
                return "Good";
            case 3:
                return "Very Good";
            case 4:
                return "Excellent";
            default:
                return "ERROR: Utility::GetRandomCondition given bad paramater";
        }
    }
    
    default int GetOfferPrice(String condition) {
        // offer different prices based on given condition
        switch (condition) {
            case "Poor":
                return GetRandomNum(1, 11);
            case "Fair":
                return GetRandomNum(10, 21);
            case "Good":
                return GetRandomNum(20, 31);
            case "Very Good":
                return GetRandomNum(30, 41);
            case "Excellent":
                return GetRandomNum(40, 51);
            default:
                Print("ERROR: Utility::GetOfferPrice given bad paramter");
                return 1000000;
        }
    }

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

    default String GetStringType() {
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

    default String GetRandomName() {
        String[] names = {
            "Alice",
            "Amber",
            "Emma",
            "Sam",
            "Ryan",
            "Nate",
            "CJ",
            "Bruce",
            "Dom",
            "Delaney",
            "Sophie",
            "Paige",
            "Mel"
        };
        return names[GetRandomNum(names.length)];
    }
}
