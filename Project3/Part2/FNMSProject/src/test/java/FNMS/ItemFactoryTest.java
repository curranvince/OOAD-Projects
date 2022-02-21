package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ItemFactoryTest {
    Item item;

    @Test
    @DisplayName("Should be able to create items")
    public void testItems() {
        for (Item.ItemType itemType : Item.ItemType.values()) {
            item = ItemFactory.MakeItem(itemType.name());
            assertTrue(item.itemType_ == itemType, "Factory should be able to create items of type" + itemType.name());
        }
    }
}