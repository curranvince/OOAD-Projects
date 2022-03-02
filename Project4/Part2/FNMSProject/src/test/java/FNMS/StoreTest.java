package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import FNMS.Item.ItemType;

public class StoreTest {
    Store store = new Store("Test Store", new NorthKitFactory());

    @Test
    @DisplayName("Should be able to create items")
    public void testConstruction() {
        // check total size of inventory
        assertTrue(store.inventory_.size() == ItemType.values().length*3, "Checking size of inventory");
        // check for 3 of each type
        for (ItemType itemType : ItemType.values()) {
            int num = 0;
            for (Item item : store.inventory_) {
                if (item.itemType_ == itemType) {
                    num++;
                }
            }
            assertTrue(num == 3, "Each type should have 3 items");
        }
    }
}