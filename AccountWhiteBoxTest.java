
package com.example.demo1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class AccountWhiteBoxTest {

    private Account verifiedAccount;
    private Account closedAccount;
    private Account suspendedAccount;

    @BeforeEach
    void setUp() {
        verifiedAccount = new Account(100.0, "Verified");
        closedAccount = new Account(100.0, "Closed");
        suspendedAccount = new Account(100.0, "Suspended");
    }

    @Test
    void deposit_closedAccount() {
        boolean result = closedAccount.deposit(50);
        assertFalse(result);
        assertEquals(100.0, closedAccount.getBalance());
    }

    @Test
    void deposit_Invalid_Amount() {
        boolean result = verifiedAccount.deposit(-20);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void deposit_zero() {
        boolean result = verifiedAccount.deposit(0);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void deposit_valid_Amount() {
        boolean result = verifiedAccount.deposit(40);
        assertTrue(result);
        assertEquals(140.0, verifiedAccount.getBalance());
    }

    @Test
    void withdraw_closedAccount() {
        boolean result = closedAccount.withdraw(30);
        assertFalse(result);
        assertEquals(100.0, closedAccount.getBalance());
    }


    @Test
    void withdraw_suspendedAccount() {
        boolean result = suspendedAccount.withdraw(30);
        assertFalse(result);
        assertEquals(100.0, suspendedAccount.getBalance());
    }

    @Test
    void withdraw_Invalid_amount() {
        boolean result = verifiedAccount.withdraw(150);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }
    @Test
    void withdraw_negative_amount() {
        boolean result = verifiedAccount.withdraw(-10);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }
    @Test
    void withdraw_zero_amount() {
        boolean result = verifiedAccount.withdraw(0);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void withdraw_validAmount() {
        boolean result = verifiedAccount.withdraw(60);
        assertTrue(result);
        assertEquals(40.0, verifiedAccount.getBalance());
    }
}
