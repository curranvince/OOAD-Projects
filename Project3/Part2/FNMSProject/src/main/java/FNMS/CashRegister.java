package FNMS;

// CashRegister class to handle the Stores $
// Good example of Cohesion because the class has
// one specifc purpose (handling money/doing simple math)
public class CashRegister {
    private int money_;

    CashRegister() { money_ = 0; }
    
    int GetAmount() { return money_; }
    
    void AddMoney(int money) { money_ += money; }

    boolean TakeMoney(int money) { 
        if ((money_ - money) >= 0) {
            money_ -= money; 
            return true;
        }
        return false;
    }
}
