import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class BankingIntegrationTest {

    private ClientController controller;
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    public void setup() {
        controller = new ClientController();

        sourceAccount = new Account(1000.0, Status.VERIFIED);
        targetAccount = new Account(0.0, Status.VERIFIED);
    }

    @Test
    @DisplayName("Integration: User Successfully Deposits Money")
    public void testDeposit() {

        String response = controller.handleDeposit(sourceAccount, "100.0");


        assertEquals("Deposit successful", response, "Controller should return success message");

        assertEquals(1100.0, sourceAccount.getBalance(), 0.001, "Account balance should increase by 100");
    }


    @Test
    @DisplayName("Integration: User Successfully Withdraws Money")
    public void testWithdraw() {

        String response = controller.handleWithdraw(sourceAccount, "50.0");

        assertEquals("Withdrawal successful", response);
        assertEquals(950.0, sourceAccount.getBalance(), 0.001);
    }


    @Test
    @DisplayName("Integration: Full Transfer Workflow Success")
    public void testTransfer() {

        String response = controller.handleTransfer(sourceAccount, targetAccount, "200.0");
        assertEquals("Transfer successful", response);

     
        assertEquals(800.0, sourceAccount.getBalance(), 0.001, "Source should decrease");
        assertEquals(200.0, targetAccount.getBalance(), 0.001, "Target should increase");
    }


    @Test
    @DisplayName("Integration: Transfer cannot happen when Target is Closed")
    public void testTransferClosed() {
   
        targetAccount.setStatus(Status.CLOSED);
        String response = controller.handleTransfer(sourceAccount, targetAccount, "200.0");
        assertEquals("Transfer failed", response);

        assertEquals(1000.0, sourceAccount.getBalance(), 0.001, "Source balance should be unchanged");
        assertEquals(0.0, targetAccount.getBalance(), 0.001, "Target balance should be unchanged");
    }

    @Test
    @DisplayName("Integration: Closed Account Cannot Withdraw")
    public void testWithdrawClosed() {
        sourceAccount.setStatus(Status.CLOSED);

        String response = controller.handleWithdraw(sourceAccount, "100");

        assertEquals("Withdrawal failed", response);
        assertEquals(1000.0, sourceAccount.getBalance(), 0.001);
    }


    @Test
    @DisplayName("Integration: Closed Account Cannot Deposit")
    public void testDepositClosed() {
        sourceAccount.setStatus(Status.CLOSED);

        String response = controller.handleDeposit(sourceAccount, "100");

        assertEquals("Deposit failed", response);
        assertEquals(1000.0, sourceAccount.getBalance(), 0.001);

    }

    @Test
    @DisplayName("Integration: Suspended Account Can Deposit")
    public void testDepositSuspended() {
        sourceAccount.setStatus(Status.SUSPENDED);

        String response = controller.handleDeposit(sourceAccount, "200");

        assertEquals("Deposit successful", response);
        assertEquals(1200.0, sourceAccount.getBalance(), 0.001,
                "Deposit should be allowed for suspended accounts");
    }


    @Test
    @DisplayName("Integration: Suspended Account Cannot Withdraw")
    public void testWithdrawSuspended() {
        sourceAccount.setStatus(Status.SUSPENDED);

        String response = controller.handleWithdraw(sourceAccount, "100.0");
        assertEquals("Withdrawal failed", response);
        assertEquals(1000.0, sourceAccount.getBalance(), 0.001, "Balance should not change");
    }

    @Test
    @DisplayName("Integration: Suspended Account Cannot Transfer")
    public void testTransferSuspended() {
        sourceAccount.setStatus(Status.SUSPENDED);

        String response = controller.handleTransfer(sourceAccount, targetAccount, "100");

        assertEquals("Transfer failed", response);

        assertEquals(1000.0, sourceAccount.getBalance(), 0.001,
                "Source balance should remain unchanged");
        assertEquals(0.0, targetAccount.getBalance(), 0.001,
                "Target balance should remain unchanged");
    }



    @Test
    @DisplayName("Integration: View Statement Formatting")
    public void testViewVerified() {

        Account testAcc = new Account(1234.56, Status.VERIFIED);
        String output = controller.handleViewStatement(testAcc);
        assertEquals("Balance: $1234.56", output);
    }
    @Test
    @DisplayName("Integration: Suspended Account Can View Balance")
    public void testViewSuspended() {
        sourceAccount.setStatus(Status.SUSPENDED);

        String output = controller.handleViewStatement(sourceAccount);
        
        assertEquals("Balance: $1000.00", output, "Suspended accounts should still be able to see balance");
    }

    @Test
    @DisplayName("Integration: Closed Account Can View Balance")
    public void testViewClosed() {

        sourceAccount.setStatus(Status.CLOSED);

        String output = controller.handleViewStatement(sourceAccount);

        assertEquals("Balance: $1000.00", output, "Closed accounts should still be able to see balance");
    }


    @Test
    @DisplayName("Integration: Controller Handles Invalid Non-Numeric Input")
    public void testInputValidation() {
    
        String response = controller.handleDeposit(sourceAccount, "NotANumber");


        assertEquals("Error: Invalid input format", response);
        assertEquals(1000.0, sourceAccount.getBalance(), 0.001, "Balance should be untouched");
    }
}