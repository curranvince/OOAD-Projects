import java.util.Scanner;
import java.util.Random;

public class Matching {
    String[] words_ = new String[] {
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

    void Run() {
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);
        int index = rand.nextInt(12);
        boolean cont = true;
        while (cont) {
            int matches = 0;
            System.out.println("Guess a 5-letter word: ");
            String guess = scanner.nextLine();
            if (guess.length() == 0) {
                System.exit(0);
            } else if (guess.length() != 5) {
                System.out.println("Please input a 5 letter word next time.");
            } else {
                guess = guess.toUpperCase();
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

                if (matches == 5) {
                    System.out.println("You've guessed the correct word! Congratulations!");
                    cont = false;
                }
            }
        }
    }
}
