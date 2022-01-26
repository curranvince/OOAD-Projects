import java.util.Scanner;
import java.util.Random;

public class Matching {
    // use same 12 words every time
    static String[] words_ = new String[] {
        "CLASS",
        "FLAGS",
        "SHADE",
        "DRIVE",
        "ARRAY",
        "ABOVE",
        "ERROR",
        "MAJOR",
        "LEARN",
        "ENTRY",
        "SKILL",
        "PITCH"
    };

    static void Run() {
        // initialize rand and scanner
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);
        // pick the index of the word to be guessed
        int index = rand.nextInt(12);
        boolean cont = true;
        while (cont) {
            
            // ask user for a guess
            System.out.println("Guess a 5-letter word: ");
            String guess = scanner.nextLine();
            if (guess.length() == 0) {
                // if empty input then exit program
                System.exit(0);
            } else if (guess.length() != 5) {
                // if guess length is too big or small report error
                System.out.println("Please input a 5 letter word next time.");
            } else {
                int matches = 0;
                // if we have valid 5 letter word conver to uppercase for comparison
                guess = guess.toUpperCase();
                // check each letter of the guess against the real word
                // give information wether each letter is correct, in the wrong position, or not in the word
                for (int i = 0; i < 5; i++) {
                    if (guess.charAt(i) == words_[index].charAt(i)) {
                        System.out.println(guess.charAt(i) + " is a match in the correct location.");
                        matches++;
                    } else if (words_[index].contains(String.valueOf(guess.charAt(i)))) {
                        System.out.println(guess.charAt(i) + " is in the word, but at a different location.");
                    } else {
                        System.out.println(guess.charAt(i) + " is not in the word");
                    }
                }
                // if all 5 letters match then tell user they win and end program
                if (matches == 5) {
                    System.out.println("You've guessed the correct word! Congratulations!");
                    cont = false;
                }
            }
        }
        scanner.close();
    }
}
