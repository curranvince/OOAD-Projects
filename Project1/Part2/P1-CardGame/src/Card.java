public class Card {
    int suit_;
    int rank_;

    // if no value given initialize a dummy card that results in error if read
    public Card() {
        suit_ = -1;
        rank_ = -1;
    }

    // initialize Card with suit and rank
    public Card(int suit, int rank) {
        suit_ = suit;
        rank_ = rank;
    }

    String ConvertSuit() {
        // convert suit int to an actual suit, error if theres no match
        switch (suit_) {
            case 0:
                return "Clubs";
            case 1:
                return "Diamonds";
            case 2:
                return "Hearts";
            case 3:
                return "Spades";
            case 98: 
            case 99:
                return "Joker";
            default:
                return "Suit Error : " + suit_;
        }
    }

    String ConvertRank() {
        switch (rank_) {
            // for regular nums convert int to string
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return String.valueOf(rank_); 
            // cases for face cards 
                case 11:
                return "Jack";
            case 12:
                return "Queen";
            case 13:
                return "King";
            case 14:
                return "Ace";
            case 98:
            case 99: 
                return "Joker";
            // if no cases match report error
            default:
                return ("Rank Error : " + String.valueOf(rank_));       
        }
    }

    void Display() {
        // display the cards properties
        System.out.println(ConvertRank() + " of " + ConvertSuit());
    }
}
