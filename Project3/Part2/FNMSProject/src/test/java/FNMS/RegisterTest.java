package FNMS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.junit.jupiter.api.*;

/**
 * Unit tests for Register class
 * https://stackoverflow.com/questions/51382356/maven-does-not-run-beforeeach-methods-while-running
 * https://www.vogella.com/tutorials/JUnit/article.html
 */
public class RegisterTest 
{
    CashRegister register = new CashRegister();

    @Test
    @DisplayName("Cash register should initialize with no money")
    public void testInit()
    {
        assertEquals(0, register.GetAmount(), "Register should have no money");
    }

    @Test
    @DisplayName("Should be able to add money to Cash Register")
    public void testAdd()
    {
        register.AddMoney(10);
        assertEquals(10, register.GetAmount(), "Register should have 10");
        register.AddMoney(10);
        assertEquals(20, register.GetAmount(), "Register should have 20");
        register.AddMoney(100);
        assertEquals(120, register.GetAmount(), "Register should have 120");
        register.AddMoney(1000);
        assertEquals(1120, register.GetAmount(), "Register should have 1120");
    }

    @Test
    @DisplayName("Should be able to take money from Cash Register")
    public void testTake()
    {
        assertFalse( register.TakeMoney(100), "Should not be able to take money from empty register" );
        assertEquals( 0, register.GetAmount(), "Register should still be 0 after taking $ when empty" );
        register.AddMoney(50);
        register.TakeMoney(20);
        assertEquals( 30, register.GetAmount(), "Register should equal 30 after adding 50 and taking 20" );
    }
}
