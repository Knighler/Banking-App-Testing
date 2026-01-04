import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditScoreServiceTest {

    @Test
    public void creditScoreAboveThreshold_shouldAllowTransaction() {
        Account account = new Account(1000, Status.VERIFIED, 700);
        assertTrue(account.isCreditEligible());
        assertTrue(account.deposit(100));
    }

    @Test
    public void creditScoreBelowThreshold_shouldBlockTransaction() {
        Account account = new Account(1000, Status.VERIFIED, 500);
        assertFalse(account.isCreditEligible());
        assertFalse(account.deposit(100));
    }

    @Test
    public void creditScoreAtThreshold_shouldAllowTransaction() {
        Account account = new Account(1000, Status.VERIFIED, 600);
        assertTrue(account.isCreditEligible());
        assertTrue(account.deposit(100));
    }

    @Test
    public void creditScoreBelowThreshold_shouldBlockWithdrawal() {
        Account account = new Account(1000, Status.VERIFIED, 500);
        assertFalse(account.withdraw(100));
    }

    @Test
    public void creditScoreAboveThreshold_shouldAllowWithdrawal() {
        Account account = new Account(1000, Status.VERIFIED, 700);
        assertTrue(account.withdraw(100));
        assertEquals(900, account.getBalance());
    }
}