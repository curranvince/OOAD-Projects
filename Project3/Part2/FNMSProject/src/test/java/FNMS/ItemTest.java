package FNMS;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ItemTest {
    Item item;

    @Test
    @DisplayName("Should be able to lower condition of items")
    public void testConditions() {
        item = ItemFactory.MakeItem("CD");
        item.condition_ = Item.Condition.excellent;
        assertTrue(item.LowerCondition());
        assertTrue(item.condition_ == Item.Condition.very_good);
        assertTrue(item.LowerCondition());
        assertTrue(item.condition_ == Item.Condition.good);
        assertTrue(item.LowerCondition());
        assertTrue(item.condition_ == Item.Condition.fair);
        assertTrue(item.LowerCondition());
        assertTrue(item.condition_ == Item.Condition.poor);
        assertFalse(item.LowerCondition());
        assertTrue(item.condition_ == Item.Condition.broken);
    }
}