# Banking GUI Test Setup Guide

## Prerequisites

### 1. Download Selenium JARs
Download these JAR files and place them in the `lib/` folder:
- selenium-java-4.15.0.jar (or latest)
- All Selenium dependencies

**Easy option:** Download from Maven Repository:
https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-java/4.15.0/

Or download the full Selenium package from:
https://www.selenium.dev/downloads/

### 2. Install ChromeDriver
1. Check your Chrome version: `chrome://version/`
2. Download matching ChromeDriver: https://chromedriver.chromium.org/downloads
3. Add ChromeDriver to your system PATH

### 3. Start the React Application
```bash
cd banking-ui
npm install     # First time only
npm start       # Starts on localhost:3000
```

## Running the Tests

### Option A: Command Line
```bash
# Make sure React app is running first!

# Compile (adjust classpath separator for your OS: ; for Windows, : for Mac/Linux)
javac -cp "lib/*" BankingGUITest.java

# Run tests
java -cp ".;lib/*" org.junit.platform.console.ConsoleLauncher --select-class=BankingGUITest
```

### Option B: VS Code
1. Ensure Java Extension Pack is installed
2. Right-click on BankingGUITest.java → Run Tests

## Required JAR Files in lib/ folder
1. junit-platform-console-standalone-1.13.0-M3.jar (already present)
2. selenium-java-4.x.x.jar
3. selenium-api-4.x.x.jar
4. selenium-chrome-driver-4.x.x.jar
5. selenium-support-4.x.x.jar
6. selenium-remote-driver-4.x.x.jar
7. Additional Selenium dependencies (guava, httpclient, etc.)

## Test Coverage

| Test Category | Tests |
|--------------|-------|
| Basic UI | Dashboard loads, Account info displays |
| Deposit | Success, Invalid amount |
| Withdraw | Success, Insufficient balance |
| Transfer | Success |
| Status Changes | To Suspended, To Closed |
| Suspended Account | Can Deposit, Cannot Withdraw/Transfer |
| Closed Account | Cannot Deposit/Withdraw, Can View Statement |
| View Statement | Shows history, Hide works |
| Input Validation | Field exists, Disabled when Closed |
| Workflows | Complete banking, Status change |

## Troubleshooting

### "ChromeDriver not found"
- Ensure chromedriver.exe is in your PATH
- Or set system property: `System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");`

### "Connection refused"
- Make sure React app is running on localhost:3000
- Run `npm start` in the banking-ui folder first

### Tests timing out
- Increase wait time in WebDriverWait
- Check if selectors match the actual HTML elements
