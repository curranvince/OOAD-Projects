package FNMS;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class CustomerTest {
    Customer customer;
    Clerk c = new Clerk("Hi", 5, new ManualTune());
    
    @Test
    @DisplayName("Should be able to make buyers")
    public void testBuyer() {
        customer = new Buyer(c);
        assertTrue(customer instanceof Buyer, "Buyer Created");
    }
    
    @Test
    @DisplayName("Should be able to make sellers")
    public void testSeller() {
        customer = new Seller(c);
        assertTrue(customer instanceof Seller, "Seller Created");
    }
}