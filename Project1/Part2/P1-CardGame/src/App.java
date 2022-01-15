import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the card drawing simulation");
        Scanner scanner = new Scanner(System.in);
        Deck deck = new Deck();
        boolean cont = true;
        while (cont) {
            System.out.println("How many cards would you like to draw?");
            int input;
            try {
                input = Integer.parseInt(scanner.nextLine());
                deck.DrawCards(input);
            } catch (Exception e) {
                System.out.println("Error: Input must be an integer");
            }
            String str_input;
            do {
                System.out.println("Would you like to draw another set of cards?");
                str_input = scanner.nextLine();
                if ((str_input.charAt(0) == 'n') || (str_input.charAt(0) == 'N') ) {
                    cont = false;
                }
            } while (str_input.charAt(0) != 'n' && str_input.charAt(0) != 'N' && str_input.charAt(0) != 'y' && str_input.charAt(0) != 'Y');  
        }
        System.out.println("Goodbye");
        scanner.close();
    }
}
