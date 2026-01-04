# Banking Application - Testing Project

A Java banking application with comprehensive unit tests, integration tests, and GUI tests using Selenium WebDriver.

## Project Structure

```
Project/
├── Account.java                 # Core account class with credit score feature
├── Status.java                  # Account status enum (VERIFIED, SUSPENDED, CLOSED, UNVERIFIED)
├── ClientController.java        # Controller for handling banking operations
├── TransactionProcessor.java    # Handles transfers between accounts
├── AccountFSMTest.java          # FSM (Finite State Machine) tests for account status
├── BankingIntegrationTest.java  # Integration tests for banking operations
├── BankingGUITest.java          # Selenium GUI tests for React frontend
├── CreditScoreServiceTest.java  # Credit score eligibility tests
├── lib/                         # Required JAR libraries
└── banking-ui/                  # React frontend application
```

## Prerequisites

- **Java JDK 11+** (recommended: JDK 17 or higher)
- **Node.js 16+** and npm (for GUI tests only)
- **Google Chrome** (for Selenium GUI tests)

## Required Libraries

All required JARs are in the `lib/` folder:

| Library | Purpose |
|---------|---------|
| `junit-platform-console-standalone-1.13.0-M3.jar` | JUnit 5 test runner |
| `selenium-java-4.39.0.jar` | Selenium WebDriver for GUI tests |
| `selenium-chrome-driver-4.39.0.jar` | Chrome browser automation |
| `guava-33.5.0-jre.jar` | Google utilities (Selenium dependency) |

## Running Unit Tests

### Option 1: Command Line (Recommended)

**Compile all Java files:**
```bash
javac -cp "lib/*" *.java
```

**Run all tests:**
```bash
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar --class-path . --scan-class-path
```

**Run a specific test class:**
```bash
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar --class-path . --select-class=CreditScoreServiceTest
```

```bash
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar --class-path . --select-class=AccountFSMTest
```

```bash
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar --class-path . --select-class=BankingIntegrationTest
```

### Option 2: Using an IDE

1. Open the project in IntelliJ IDEA, Eclipse, or VS Code
2. Add all JARs from `lib/` folder to your project's classpath
3. Right-click on any test file and select "Run Tests"

## Running GUI Tests (Selenium)

GUI tests require the React frontend to be running.

### Step 1: Start the React Application

```bash
cd banking-ui
npm install        # First time only
npm start          # Starts on http://localhost:3000
```

### Step 2: Run GUI Tests

In a separate terminal:

```bash
# Compile with Selenium libraries
javac -cp "lib/*" *.java

# Run GUI tests
java -cp ".;lib/*" org.junit.platform.console.ConsoleLauncher --select-class=BankingGUITest
```

> **Note:** On Linux/Mac, use `:` instead of `;` in the classpath:
> ```bash
> java -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher --select-class=BankingGUITest
> ```

## Test Descriptions

### CreditScoreServiceTest.java
Tests the credit score eligibility feature:
- Credit score ≥ 600: Transactions allowed
- Credit score < 600: Transactions blocked

### AccountFSMTest.java
Tests account status state transitions:
- UNVERIFIED → VERIFIED → SUSPENDED → CLOSED
- Valid and invalid state transitions

### BankingIntegrationTest.java
Tests integration between Account, ClientController, and TransactionProcessor:
- Deposits, withdrawals, transfers
- Error handling for invalid inputs

### BankingGUITest.java
Selenium tests for the React frontend:
- UI element verification
- Button interactions
- Status changes
- Transaction workflows

## Troubleshooting

### "ClassNotFoundException" when running tests
Make sure all JARs are in the classpath:
```bash
java -cp ".;lib/*" ...   # Windows
java -cp ".:lib/*" ...   # Linux/Mac
```

### ChromeDriver issues
- Ensure Google Chrome is installed
- Selenium Manager (included) will auto-download the correct ChromeDriver

### React app not starting
```bash
cd banking-ui
rm -rf node_modules
npm install
npm start
```

## Authors

ASU Fall 2025 - Software Testing Project
