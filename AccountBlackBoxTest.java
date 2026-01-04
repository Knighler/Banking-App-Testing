import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.*;

public class AccountBlackBoxTest {

    private Account verifiedAccount;
    private Account suspendedAccount;
    private Account closedAccount;

    @BeforeEach
    void setUp() {
        verifiedAccount = new Account(100.0, "Verified");
        suspendedAccount = new Account(100.0, "Suspended");
        closedAccount = new Account(100.0, "Closed");
    }

    // =======================
    // Deposit Tests
    // =======================

    @Test
    void BB01_depositNegativeAmount_shouldFail() {
        boolean result = verifiedAccount.deposit(-100);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void BB02_depositZero_shouldFail() {
        boolean result = verifiedAccount.deposit(0);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void BB03_depositValidAmount_shouldSucceed() {
        boolean result = verifiedAccount.deposit(50);
        assertTrue(result);
        assertEquals(150.0, verifiedAccount.getBalance());
    }

    @Test
    void BB04_depositInClosedAccount_shouldFail() {
        boolean result = closedAccount.deposit(50);
        assertFalse(result);
        assertEquals(100.0, closedAccount.getBalance());
    }

    @Test
    void BB05_depositInSuspendedAccount_shouldSucceed() {
        boolean result = suspendedAccount.deposit(50);
        assertTrue(result);
        assertEquals(150.0, suspendedAccount.getBalance());
    }

    // =======================
    // Withdraw Tests
    // =======================

    @Test
    void BB06_withdrawValidAmount_shouldSucceed() {
        boolean result = verifiedAccount.withdraw(50);
        assertTrue(result);
        assertEquals(50.0, verifiedAccount.getBalance());
    }

    @Test
    void BB07_withdrawExactBalance_shouldSucceed() {
        boolean result = verifiedAccount.withdraw(100);
        assertTrue(result);
        assertEquals(0.0, verifiedAccount.getBalance());
    }

    @Test
    void BB08_withdrawMoreThanBalance_shouldFail() {
        boolean result = verifiedAccount.withdraw(150);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }

    @Test
    void BB09_withdrawInSuspendedAccount_shouldFail() {
        boolean result = suspendedAccount.withdraw(50);
        assertFalse(result);
        assertEquals(100.0, suspendedAccount.getBalance());
    }

    @Test
    void BB10_withdrawInClosedAccount_shouldFail() {
        boolean result = closedAccount.withdraw(50);
        assertFalse(result);
        assertEquals(100.0, closedAccount.getBalance());
    }

    @Test
    void BB11_withdrawNegativeAmount_shouldFail() {
        boolean result = verifiedAccount.withdraw(-10);
        assertFalse(result);
        assertEquals(100.0, verifiedAccount.getBalance());
    }
}
