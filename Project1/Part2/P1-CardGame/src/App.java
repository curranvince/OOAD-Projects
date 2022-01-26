import java.util.Scanner;

public class App {
    static void Run() {
        // welcome user and initialize scanner and deck
        System.out.println("Welcome to the card drawing simulation");
        Scanner scanner = new Scanner(System.in);
        Deck deck = new Deck();
        boolean cont = true;
        while (cont) {
            // get user input
            System.out.println("How many cards would you like to draw?");
            int input;
            // check user inout is valid
            try {
                // draw card for valid input
                input = Integer.parseInt(scanner.nextLine());
                deck.DrawCards(input);
            } catch (Exception e) {
                // print error if input is invalid
                System.out.println("Error: Input must be an integer");
            }
            String str_input;
            do {
                // see if user wants to draw more cards
                System.out.println("Would you like to draw another set of cards?");
                str_input = scanner.nextLine();
                if ((str_input.charAt(0) == 'n') || (str_input.charAt(0) == 'N') ) {
                    // if the user doesnt want to draw another card end the program
                    cont = false;
                }
            } while (str_input.charAt(0) != 'n' && str_input.charAt(0) != 'N' && str_input.charAt(0) != 'y' && str_input.charAt(0) != 'Y');  
        }
        // say bye and close the scanner
        System.out.println("Goodbye");
        scanner.close();
    }

    public static void main(String[] args) throws Exception {
        Run();
    }
}
