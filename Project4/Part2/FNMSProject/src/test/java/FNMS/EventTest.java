package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class EventTest {
    Store store = new Store("Test Store", new NorthKitFactory());
    Store store2 = new Store("Different Store", new NorthKitFactory());
    AbstractClerk clerk = new Clerk("Tester", 10, new ElectronicTune());

    @Test
    @DisplayName("Should be able say events are equal based off type and store")
    public void testEquals() {
        store.UpdateClerk(clerk);
        store2.UpdateClerk(clerk);
        MyEvent event = new ArrivalEvent(store);
        MyEvent event2 = new ArrivalEvent(store);
        MyEvent event3 = new ItemsAddedEvent(1, store);
        MyEvent event4 = new ArrivalEvent(store2);
        MyEvent event5 = new ItemsAddedEvent(2, store);
        assertTrue(event.equals(event2), "same event at same store should be equal");
        assertFalse(event.equals(event3), "different event at same store should not be equal");
        assertFalse(event.equals(event4), "events at different stores should not be equal");
        assertTrue(event3.equals(event5), "same event at same store with different data should be equal");
    }
}