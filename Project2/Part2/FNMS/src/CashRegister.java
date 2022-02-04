public class CashRegister {
    int money_;

    CashRegister() {
        money_ = 0;
    }
    
    void AddMoney(int money) { money_ += money; }

    boolean HasEnough(int money) { return ((money_ - money) > 0); }

    void TakeMoney(int money) { money_ -= money; }
}
