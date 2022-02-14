interface Subscriber {
    public Update(String context);
}

class Logger implements Subscriber{ 
    public Update(String context) {

    }
}

class Tracker implements Subscriber {
    int[3][3] stats_;
    // [0][] for Velma, [1][] for Shaggy, [2][] for Daphne
    // [][0] for sold, [][1] for purchased, [][2] for damaged

    public Update(String context) {

    }
}