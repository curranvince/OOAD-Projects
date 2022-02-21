package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class CustomerTest {
    Customer customer;

    @Test
    @DisplayName("Customers should have items")
    public void testItems() {
        customer = new Buyer();
        assertTrue(customer.GetItem() != null, "Buyer should have an item");
        customer = new Seller();
        assertTrue(customer.GetItem() != null, "Seller should have an item");
    }

    @Test
    @DisplayName("Should be able to make buyers")
    public void testBuyer() {
        customer = new Buyer();
        assertTrue(customer.MakeRequest() == Customer.RequestType.Buy, "Buyer should return buy");
    }

    @Test
    @DisplayName("Should be able to make sellers")
    public void testSeller() {
        customer = new Seller();
        assertTrue(customer.MakeRequest() == Customer.RequestType.Sell, "Seller should return sell");
    }
}