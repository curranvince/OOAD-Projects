package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class TuneTest {
    Store store = new Store();
    AbstractClerk clerk;

    @Test
    @DisplayName("Should be able to electronically tune items")
    public void testElectronicTune() {
        AbstractClerk clerk = new Clerk("Tester", 10, new ElectronicTune(), store);
        for (Item item : store.inventory_) {
            if (item.GetComponent(Tuneable.class) != null) {
                clerk.tune_strategy_.Execute(item.GetComponent(Tuneable.class));
                assertTrue(item.GetComponent(Tuneable.class).tuned_ == true, "all electronically tuned items should be tuned");
            }
        }
    }
}