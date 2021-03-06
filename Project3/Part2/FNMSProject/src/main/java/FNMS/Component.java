package FNMS;

abstract class Component {}

class Tuneable extends Component {
    private boolean tuned_;

    Tuneable() { tuned_ = false; }

    void Tune() { tuned_ = true; };
    void Untune() { tuned_ = false; };
    boolean IsTuned() { return tuned_; }
}

class Electric extends Component {}