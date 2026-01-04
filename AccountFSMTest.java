import org.junit.Test;
import static org.junit.Assert.*;

public class AccountFSMTest {

    /* =========================
       VIEW TRANSITIONS
       ========================= */

    @Test
    public void view_unverified_shouldReturnBalance() {
        Account acc = new Account(100, Status.UNVERIFIED);
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void view_verified_shouldReturnBalance() {
        Account acc = new Account(100, Status.VERIFIED);
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void view_suspended_shouldReturnBalance() {
        Account acc = new Account(200, Status.SUSPENDED);
        assertEquals(200, acc.getBalance(), 0.0001);
    }

    @Test
    public void view_closed_shouldReturnBalance() {
        Account acc = new Account(300, Status.CLOSED);
        assertEquals(300, acc.getBalance(), 0.0001);
    }

    /* =========================
       DEPOSIT TRANSITIONS
       ========================= */

    @Test
    public void deposit_unverified_shouldFail() {
        Account acc = new Account(100, Status.UNVERIFIED);
        assertFalse(acc.deposit(50));
        assertEquals(100, acc.getBalance(), 0.0001);    }

    @Test
    public void deposit_verified_validAmount_shouldSucceed() {
        Account acc = new Account(100, Status.VERIFIED);
        assertTrue(acc.deposit(50));
        assertEquals(150, acc.getBalance(), 0.0001);
    }

    @Test
    public void deposit_verified_invalidAmount_shouldFail() {
        Account acc = new Account(100, Status.VERIFIED);
        assertFalse(acc.deposit(0));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void deposit_suspended_validAmount_shouldSucceed() {
        Account acc = new Account(100, Status.SUSPENDED);
        assertTrue(acc.deposit(50));
        assertEquals(150, acc.getBalance(), 0.0001);
    }

    @Test
    public void deposit_suspended_invalidAmount_shouldFail() {
        Account acc = new Account(100, Status.SUSPENDED);
        assertFalse(acc.deposit(-50));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void deposit_closed_shouldFail() {
        Account acc = new Account(100, Status.CLOSED);
        assertFalse(acc.deposit(50));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    /* =========================
       WITHDRAW TRANSITIONS
       ========================= */

    @Test
    public void withdraw_unverified_shouldFail() {
        Account acc = new Account(100, Status.UNVERIFIED);
        assertFalse(acc.withdraw(10));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void withdraw_verified_sufficientBalance_shouldSucceed() {
        Account acc = new Account(100, Status.VERIFIED);
        assertTrue(acc.withdraw(40));
        assertEquals(60, acc.getBalance(), 0.0001);
    }

    @Test
    public void withdraw_verified_insufficientBalance_shouldFail() {
        Account acc = new Account(100, Status.VERIFIED);
        assertFalse(acc.withdraw(200));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void withdraw_suspended_shouldFail() {
        Account acc = new Account(100, Status.SUSPENDED);
        assertFalse(acc.withdraw(10));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    @Test
    public void withdraw_closed_shouldFail() {
        Account acc = new Account(100, Status.CLOSED);
        assertFalse(acc.withdraw(10));
        assertEquals(100, acc.getBalance(), 0.0001);
    }

    /* =========================
       TRANSFER TRANSITIONS
       ========================= */

    @Test
    public void transfer_unverifiedToVerified_shouldFail() {
        Account source = new Account(200, Status.UNVERIFIED);
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertFalse(result);
        assertEquals(200, source.getBalance(), 0.0001);
        assertEquals(100, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_verifiedToVerified_shouldSucceed() {
        Account source = new Account(200, Status.VERIFIED);
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertTrue(result);
        assertEquals(150, source.getBalance(), 0.0001);
        assertEquals(150, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_insufficientBalance_shouldFail() {
        Account source = new Account(20, Status.VERIFIED);
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertFalse(result);
        assertEquals(20, source.getBalance(), 0.0001);
        assertEquals(100, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_fromSuspended_shouldFail() {
        Account source = new Account(200, Status.SUSPENDED);
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertFalse(result);
        assertEquals(200, source.getBalance(), 0.0001);
        assertEquals(100, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_toClosed_shouldFail() {
        Account source = new Account(200, Status.VERIFIED);
        Account target = new Account(100, Status.CLOSED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertFalse(result);
        assertEquals(200, source.getBalance(), 0.0001);
        assertEquals(100, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_fromClosed_shouldFail() {
        Account source = new Account(200, Status.CLOSED);
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        boolean result = tp.processTransfer(source, target, 50);

        assertFalse(result);
        assertEquals(200, source.getBalance(), 0.0001);
        assertEquals(100, target.getBalance(), 0.0001);
    }

    @Test
    public void transfer_nullSource_shouldFail() {
        Account target = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        assertFalse(tp.processTransfer(null, target, 50));
    }

    @Test
    public void transfer_nullTarget_shouldFail() {
        Account source = new Account(100, Status.VERIFIED);
        TransactionProcessor tp = new TransactionProcessor();

        assertFalse(tp.processTransfer(source, null, 50));
    }

    /* =========================
       STATE TRANSITIONS
       ========================= */

    @Test
    public void unverified_to_verified_transition() {
        Account acc = new Account(100, Status.UNVERIFIED);

        assertTrue(acc.verify());
        assertEquals(Status.VERIFIED, acc.getStatus());
    }

    @Test
    public void verified_to_suspended_transition() {
        Account acc = new Account(100, Status.VERIFIED);

        assertTrue(acc.violation());
        assertEquals(Status.SUSPENDED, acc.getStatus());
    }

    @Test
    public void suspended_to_verified_transition() {
        Account acc = new Account(100, Status.SUSPENDED);

        assertTrue(acc.appeal());
        assertEquals(Status.VERIFIED, acc.getStatus());
    }

    @Test
    public void suspended_to_closed_transition() {
        Account acc = new Account(100, Status.SUSPENDED);

        assertTrue(acc.adminAction());
        assertEquals(Status.CLOSED, acc.getStatus());
    }

}