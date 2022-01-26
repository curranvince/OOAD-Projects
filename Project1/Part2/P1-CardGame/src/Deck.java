import java.util.Random;

public class Deck {
    // a deck is a collection of cards
    private Card[] cards_;
    // current number of cards in deck
    private int num_cards_;

    public Deck() {
        // initialize deck with 54 cards every time
        cards_ = new Card[54];
        Reset();
    }

    void ShowDeck() {
        // print the whole deck
        for (Card card  : cards_) {
            card.Display();
        }
    }

    int ChooseCard() {
        // return a random index in rage of cards available
        Random rand = new Random();
        int rando = rand.nextInt(num_cards_);
        return rando;
    }

    void DrawCards(int n) {
        System.out.println("Drawing " + String.valueOf(n) + " cards");
        if (n < 0) {
            // print error for negative input
            System.out.println("Error: Input must be 0 or greater");
        } else if (n > 54) {
            // print error for input too high
            System.out.println("Error: Input must be 54 or less");
        } else if (n == 0) {
            // print blank line if they want 0 cards
            System.out.println("");
        } else {
            // for valid input draw however many cards user wants
            for (int i = 0; i < n; i ++) {
                int c_i = ChooseCard();
                cards_[c_i].Display();
                RemoveCard(c_i);
            }
        }
        // reset the deck after every draw of cards
        Reset();
    }

    void RemoveCard(int index) {
        // move every card after the one to remove to the left
        for (int i = index; i < num_cards_ - 1; i++) {
            cards_[i] = cards_[i + 1];
        }
        num_cards_--;
    }

    void Reset() {
        // reset deck to initial state
        num_cards_ = 54;
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < 15; j++) {
                cards_[counter] = new Card(i, j);
                counter++;
            }
        }

        cards_[52] = new Card(99, 99);
        cards_[53] = new Card(99,99);
    }
}
