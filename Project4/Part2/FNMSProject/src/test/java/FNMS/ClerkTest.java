package FNMS;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class ClerkTest {
    DateTimeFormatter date_time_ = DateTimeFormatter.ofPattern("HH:mm");
    List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    private String[] clerkNames_ = {"Velma", "Daphne", "Norville", "Fred", "Shaggy", "Scooby"};
    Store s = new Store("sample store", new SouthKitFactory());
    Clerk c = new Clerk("Lol", 10, new ManualTune());

    @Test
    @DisplayName("Should be able to get the clerk's name when asked by the customer.")
    public void testNameRequest() {
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[0], 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[1], 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[2], 15, new ManualTune())));
        clerks_.add(new Clerk(clerkNames_[3], 15, new ManualTune()));
        clerks_.add(new Clerk(clerkNames_[4], 20, new ElectronicTune()));
        clerks_.add(new Clerk(clerkNames_[5], 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            assertTrue(clerks_.get(i).GetName() == clerkNames_[i], "the clerks should yield their proper name upon user-customer requesting the clerk's name");
        }
    }

    @Test
    @DisplayName("Should be able to get the curren time name when asked by the customer.")
    public void testTimeRequest() {
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[0], 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[1], 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(clerkNames_[2], 15, new ManualTune())));
        clerks_.add(new Clerk(clerkNames_[3], 15, new ManualTune()));
        clerks_.add(new Clerk(clerkNames_[4], 20, new ElectronicTune()));
        clerks_.add(new Clerk(clerkNames_[5], 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            String time_clerk = clerks_.get(i).GetTime().substring(0, 5);

            String time_current = date_time_.format(LocalTime.now());
            assertTrue(time_clerk.equals(time_current), "current local time should be equal ignoring seconds with the clerk's current local time");
        }
    }
    @Test
    @DisplayName("Should be able to check if store bought item")
    public void testBuy() {
        Item item = ItemFactory.MakeItem("CD");
        s.UpdateClerk(c);
        c.GoToBank();
        assertTrue(c.Buy(item, 5), "Bought Item");
    }

    @Test
    @DisplayName("Should be able to get the 1000 bucks when invoked")
    public void testGoToBank() {
        s.UpdateClerk(c);
        c.GoToBank();
        assertTrue((s.register_.GetAmount() == 1000), "Got 1000 bucks");
    }
}


// if (GetNumItemsByType(itemType) == 0 && !store_.discontinued_.contains(itemType)) store_.Discontiue(itemType);        
// @Test
// @DisplayName("Should be able to create store with items")
// public void testItems() {
//     for (Item.ItemType itemType : Item.ItemType.values()) {
//         item = ItemFactory.MakeItem(itemType.name());
//         assertTrue(item.itemType_ == itemType, "Factory should be able to create items of type" + itemType.name());
//     }
// }