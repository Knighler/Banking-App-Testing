import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class BankingGUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:3000";

    @BeforeAll
    public static void setupClass() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); 
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeEach
    public void setup() {
        driver.get(BASE_URL);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dashboard")));
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- HELPER METHOD: The Fix for Balance Tests ---
    private String getBalanceText() {
        try {
            WebElement balanceParent = driver.findElement(By.xpath("//p[strong[contains(text(), 'Balance:')]]"));
            return balanceParent.getText(); 
        } catch (NoSuchElementException e) {
            return ""; 
        }
    }

    // ==================== BASIC UI TESTS ====================

    @Test
    @DisplayName("Dashboard Loads Successfully")
    public void testDashboardLoads() {
        WebElement heading = driver.findElement(By.tagName("h2"));
        assertEquals("Client Dashboard", heading.getText());
    }

    @Test
    @DisplayName("Account Info Displays Correctly")
    public void testAccountInfoDisplays() {
        WebElement clientName = driver.findElement(By.xpath("//*[contains(text(), 'Mariam Riyad')]"));
        WebElement accountNumber = driver.findElement(By.xpath("//*[contains(text(), '123456')]"));
        
        assertNotNull(clientName, "Client name should be displayed");
        assertNotNull(accountNumber, "Account number should be displayed");
    }

    @Test
    @DisplayName("Initial Balance Shows $1000.00")
    public void testInitialBalance() {
        String balanceText = getBalanceText();
        assertTrue(balanceText.contains("$1000.00"), 
            "Initial balance should be $1000.00, but found: '" + balanceText + "'");
    }

    // ==================== DEPOSIT TESTS ====================

    @Test
    @DisplayName("Successful Deposit Updates Balance")
    public void testDepositSuccess() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("100");

        driver.findElement(By.xpath("//button[text()='Deposit']")).click();

        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("message")));
        assertTrue(message.getText().contains("Deposit") && message.getText().contains("successful"),
            "Should show deposit success message");

        String balanceText = getBalanceText();
        assertTrue(balanceText.contains("$1100.00"), 
            "Balance should be updated to $1100.00");
    }

    @Test
    @DisplayName("Deposit with Invalid Amount Shows Error")
    public void testDepositInvalidAmount() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("-50");

        driver.findElement(By.xpath("//button[text()='Deposit']")).click();

        WebElement message = driver.findElement(By.className("message"));
        String msg = message.getText().toLowerCase();
        assertTrue(msg.contains("invalid") || msg.contains("error"),
            "Should show error for invalid amount");
    }

    // ==================== WITHDRAWAL TESTS ====================

    @Test
    @DisplayName("Successful Withdrawal Updates Balance")
    public void testWithdrawSuccess() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("200");

        driver.findElement(By.xpath("//button[text()='Withdraw']")).click();

        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("message")));
        assertTrue(message.getText().contains("Withdrawal") && message.getText().contains("successful"));

        String balanceText = getBalanceText();
        assertTrue(balanceText.contains("$800.00"), 
            "Balance should be updated to $800.00");
    }

    @Test
    @DisplayName("Withdrawal Exceeding Balance Shows Error")
    public void testWithdrawInsufficientBalance() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("5000");

        driver.findElement(By.xpath("//button[text()='Withdraw']")).click();

        WebElement message = driver.findElement(By.className("message"));
        String msg = message.getText().toLowerCase();
        assertTrue(msg.contains("insufficient") || msg.contains("error"));
    }

    // ==================== TRANSFER TESTS ====================

    @Test
    @DisplayName("Successful Transfer Updates Balance")
    public void testTransferSuccess() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("150");

        driver.findElement(By.xpath("//button[contains(text(),'Transfer')]")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transfer-options")));

        WebElement targetAccountSelect = driver.findElement(By.cssSelector(".target-account-select"));
        Select select = new Select(targetAccountSelect);
        select.selectByValue("789012");

        WebElement confirmBtn = driver.findElement(By.cssSelector(".confirm-transfer-btn"));
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));
        confirmBtn.click();

        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("message")));
        assertTrue(message.getText().contains("Transfer") && message.getText().contains("completed"));

        String balanceText = getBalanceText();
        assertTrue(balanceText.contains("$850.00"), 
            "Balance should be updated to $850.00");
    }

    @Test
    @DisplayName("Transfer Without Target Account Shows Error")
    public void testTransferNoTargetAccount() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("100");

        driver.findElement(By.xpath("//button[contains(text(),'Transfer')]")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transfer-options")));

        WebElement confirmBtn = driver.findElement(By.cssSelector(".confirm-transfer-btn"));
        assertFalse(confirmBtn.isEnabled(), 
            "Confirm button should be disabled when no target account is selected");
    }

    @Test
    @DisplayName("Transfer Options Hidden Initially")
    public void testTransferOptionsHiddenInitially() {
        assertTrue(driver.findElements(By.cssSelector(".transfer-options")).isEmpty(),
            "Transfer options should be hidden initially");
    }

    @Test
    @DisplayName("Cancel Transfer Hides Options")
    public void testCancelTransferHidesOptions() {
        WebElement transferBtn = driver.findElement(By.xpath("//button[contains(text(),'Transfer')]"));
        transferBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transfer-options")));

        WebElement cancelBtn = driver.findElement(By.xpath("//button[contains(text(),'Cancel Transfer')]"));
        cancelBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".transfer-options")));
        assertTrue(driver.findElements(By.cssSelector(".transfer-options")).isEmpty(),
            "Transfer options should be hidden after cancel");
    }

    // ==================== STATUS CHANGE TESTS ====================

    @Test
    @DisplayName("Change Status to Suspended")
    public void testChangeStatusToSuspended() {
        driver.findElement(By.cssSelector(".status-card.suspended")).click();

        WebElement statusBadge = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".status-badge.status-suspended")));
        
        // FIXED: toLowerCase() for CSS uppercase transform
        assertTrue(statusBadge.getText().toLowerCase().contains("suspended"), 
            "Status should change to Suspended");
    }

    @Test
    @DisplayName("Change Status to Closed")
    public void testChangeStatusToClosed() {
        driver.findElement(By.cssSelector(".status-card.closed")).click();

        WebElement statusBadge = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".status-badge.status-closed")));
        
        // FIXED: toLowerCase()
        assertTrue(statusBadge.getText().toLowerCase().contains("closed"), 
            "Status should change to Closed");
    }

    // ==================== SUSPENDED ACCOUNT TESTS ====================

    @Test
    @DisplayName("Suspended Account Can Deposit")
    public void testSuspendedCanDeposit() {
        driver.findElement(By.cssSelector(".status-card.suspended")).click();
        
        // Wait for badge update
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-suspended")));

        WebElement depositBtn = driver.findElement(By.xpath("//button[text()='Deposit']"));
        assertTrue(depositBtn.isEnabled(), "Deposit should be enabled for Suspended accounts");

        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("50");
        depositBtn.click();

        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("message")));
        assertTrue(message.getText().contains("successful"), 
            "Deposit should succeed for Suspended accounts");
    }

    @Test
    @DisplayName("Suspended Account Cannot Withdraw")
    public void testSuspendedCannotWithdraw() {
        driver.findElement(By.cssSelector(".status-card.suspended")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-suspended")));

        WebElement withdrawBtn = driver.findElement(By.xpath("//button[text()='Withdraw']"));
        assertFalse(withdrawBtn.isEnabled(), "Withdraw should be disabled for Suspended accounts");
    }

    @Test
    @DisplayName("Suspended Account Cannot Transfer")
    public void testSuspendedCannotTransfer() {
        driver.findElement(By.cssSelector(".status-card.suspended")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-suspended")));

        WebElement transferBtn = driver.findElement(By.xpath("//button[contains(text(),'Transfer')]"));
        assertFalse(transferBtn.isEnabled(), "Transfer should be disabled for Suspended accounts");
    }

    // ==================== CLOSED ACCOUNT TESTS ====================

    @Test
    @DisplayName("Closed Account Cannot Deposit")
    public void testClosedCannotDeposit() {
        driver.findElement(By.cssSelector(".status-card.closed")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-closed")));

        WebElement depositBtn = driver.findElement(By.xpath("//button[text()='Deposit']"));
        assertFalse(depositBtn.isEnabled(), "Deposit should be disabled for Closed accounts");
    }

    @Test
    @DisplayName("Closed Account Cannot Withdraw")
    public void testClosedCannotWithdraw() {
        driver.findElement(By.cssSelector(".status-card.closed")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-closed")));

        WebElement withdrawBtn = driver.findElement(By.xpath("//button[text()='Withdraw']"));
        assertFalse(withdrawBtn.isEnabled(), "Withdraw should be disabled for Closed accounts");
    }

    @Test
    @DisplayName("Closed Account Can View Statement")
    public void testClosedCanViewStatement() {
        driver.findElement(By.cssSelector(".status-card.closed")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-closed")));

        WebElement viewStatementBtn = driver.findElement(By.xpath("//button[contains(text(),'Statement')]"));
        assertTrue(viewStatementBtn.isEnabled(), "View Statement should be enabled for Closed accounts");

        viewStatementBtn.click();
        WebElement statement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("statement")));
        assertNotNull(statement, "Statement should be visible");
    }

    // ==================== VIEW STATEMENT TESTS ====================

    @Test
    @DisplayName("View Statement Shows Transaction History")
    public void testViewStatementShowsHistory() {
        WebElement viewStatementBtn = driver.findElement(By.xpath("//button[contains(text(),'Statement')]"));
        viewStatementBtn.click();

        WebElement statement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("statement")));
        WebElement header = statement.findElement(By.tagName("h3"));
        
        assertEquals("Transaction History", header.getText());
    }

    @Test
    @DisplayName("Hide Statement Button Works")
    public void testHideStatement() {
        WebElement viewStatementBtn = driver.findElement(By.xpath("//button[contains(text(),'Statement')]"));
        viewStatementBtn.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("statement")));

        WebElement hideStatementBtn = driver.findElement(By.xpath("//button[contains(text(),'Hide Statement')]"));
        hideStatementBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("statement")));
        assertTrue(driver.findElements(By.className("statement")).isEmpty(), 
            "Statement should be hidden");
    }

    // ==================== INPUT VALIDATION TESTS ====================

    @Test
    @DisplayName("Amount Input Field Exists and Accepts Numbers")
    public void testAmountInputField() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        
        assertNotNull(amountInput, "Amount input field should exist");
        assertTrue(amountInput.isEnabled(), "Amount input should be enabled");
        
        amountInput.sendKeys("123.45");
        assertEquals("123.45", amountInput.getAttribute("value"), 
            "Input should accept decimal numbers");
    }

    @Test
    @DisplayName("Closed Account Disables Amount Input")
    public void testClosedAccountDisablesInput() {
        driver.findElement(By.cssSelector(".status-card.closed")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".status-badge.status-closed")));

        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        assertFalse(amountInput.isEnabled(), "Amount input should be disabled for Closed accounts");
    }

    // ==================== WORKFLOW TESTS ====================

    @Test
    @DisplayName("Complete Banking Workflow - Deposit then Withdraw")
    public void testCompleteWorkflow() {
        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        
        // 1. Deposit
        amountInput.clear();
        amountInput.sendKeys("500");
        driver.findElement(By.xpath("//button[text()='Deposit']")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("message"), "successful"));

        // 2. Withdraw
        amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        amountInput.clear();
        amountInput.sendKeys("200");
        driver.findElement(By.xpath("//button[text()='Withdraw']")).click();
        
        // Wait for balance update
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
             By.xpath("//p[strong[contains(text(), 'Balance:')]]"), "$1300.00"));

        String balanceText = getBalanceText();
        assertTrue(balanceText.contains("$1300.00"), 
            "Final balance should be $1300.00");
    }

@Test
    @DisplayName("Status Change Workflow - Verified to Suspended to Verified")
    public void testStatusChangeWorkflow() {
        // Start as Verified
        WebElement statusBadge = driver.findElement(By.className("status-badge"));
        assertTrue(statusBadge.getText().toLowerCase().contains("verified"));

        // 1. Change to Suspended
        driver.findElement(By.cssSelector(".status-card.suspended")).click();
        
        // FIX: Wait for the specific CLASS (.status-suspended) instead of TEXT
        // This ignores the uppercase/lowercase issue entirely.
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".status-badge.status-suspended")));

        // Verify Withdraw is disabled
        WebElement withdrawBtn = driver.findElement(By.xpath("//button[text()='Withdraw']"));
        assertFalse(withdrawBtn.isEnabled(), "Withdraw should be disabled when Suspended");

        // 2. Change back to Verified
        driver.findElement(By.cssSelector(".status-card.verified")).click();
        
        // Wait for the Verified class to return
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".status-badge.status-verified")));

        // Verify Withdraw is enabled again
        withdrawBtn = driver.findElement(By.xpath("//button[text()='Withdraw']"));
        assertTrue(withdrawBtn.isEnabled(), "Withdraw should be enabled when Verified");
    }
}