package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ClerkTest {
    DateTimeFormatter date_time_ = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalTime current_localtime_ = LocalTime.now();
    List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    private String[] clerkNames_ = {"Velma","Daphne","Norville","Fred","Shaggy","Scooby"};
    @Test
    @DisplayName("Should be able to get the clerk's name when asked by the customer.")
    public void testNameRequest() {
        clerks_.add(new ClerkSellDecorator(new Clerk("Velma", 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Daphne", 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Norville", 15, new ManualTune())));
        clerks_.add(new Clerk("Fred", 15, new ManualTune()));
        clerks_.add(new Clerk("Shaggy", 20, new ElectronicTune()));
        clerks_.add(new Clerk("Scooby", 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            assertTrue(clerks_.get(i).GetName() == clerkNames_[i]);
        }
    }
    @Test
    @DisplayName("Should be able to get the curren time name when asked by the customer.")
    public void testTimeRequest() {
        clerks_.add(new ClerkSellDecorator(new Clerk("Velma", 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Daphne", 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Norville", 15, new ManualTune())));
        clerks_.add(new Clerk("Fred", 15, new ManualTune()));
        clerks_.add(new Clerk("Shaggy", 20, new ElectronicTune()));
        clerks_.add(new Clerk("Scooby", 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            assertTrue(clerks_.get(i).GetTime() == date_time_.format(current_localtime_));
        }
    }
}



// if (GetNumItemsByType(itemType) == 0 && !store_.discontinued_.contains(itemType)) store_.Discontiue(itemType);        @Test
// @DisplayName("Should be able to create store with items")
// public void testItems() {
//     for (Item.ItemType itemType : Item.ItemType.values()) {
//         item = ItemFactory.MakeItem(itemType.name());
//         assertTrue(item.itemType_ == itemType, "Factory should be able to create items of type" + itemType.name());
//     }
// }