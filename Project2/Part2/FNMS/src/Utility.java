import java.util.*;
import java.util.Random;

class Pair {
    String album_;
    String artist_;
    Pair(String album, String artist) { 
        album_ = album;
        artist_ = artist;
    }
}
public class Utility {
    

    static Random random = new Random();

    static int GetRandomNum(int range) { return random.nextInt(range); }
    static int GetRandomNum(int min, int max) { return random.nextInt(max-min)+min; }

    static Size GetRandomSize() { return Size.values()[GetRandomNum(3)]; }
    static ItemType GetRandomItemType() { return ItemType.values()[GetRandomNum(17)]; }
    
    static Vector<Customer> MakeCustomers() {
        Vector<Customer> toServe = new Vector<Customer>();
        int buyers = Utility.GetRandomNum(4, 11);
        int sellers = Utility.GetRandomNum(1, 5);
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
    
    static String GetRandomCondition() {
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
                return "ERROR: Utility::GetRandomCondition";
        }
    }
    
    static int GetOfferPrice(String condition) {
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
                return 100000;
        }
    }

    static String LowerCondition(String condition) {
        switch (condition) {
            case "Excellent":
                return "Very Good";
            case "Very Good":
                return "Good";
            case "Good":
                return "Fair";
            case "Fair":
                return "Poor";
            default:
                return "ERROR: Utility::LowerCondition";
        }
    }

    static String[] GetMusicTitle() {
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
            {"Vack to Black", "Amy Winehouse"},
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
}
