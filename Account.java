public class Account {

    private double balance;
    private Status status;
    private int creditScore;
    private static final int CREDIT_SCORE_THRESHOLD = 600;

    // we didn't change the methods, only changed string statuses to enum Status

    public Account(double initialBalance, Status initialStatus) {
        this.balance = initialBalance;
        this.status = initialStatus;
        this.creditScore = 700; // Default credit score
    }

    public Account(double initialBalance, Status initialStatus, int creditScore) {
        this.balance = initialBalance;
        this.status = initialStatus;
        this.creditScore = creditScore;
    }

    public boolean deposit(double amount) {
        // Check credit eligibility first
        if (!isCreditEligible()) return false;
        
        // PROPOSED FIX: add unverified check
        if (status == Status.CLOSED || status == Status.UNVERIFIED || amount <= 0) return false;

        // ORIGINAL LINE
        // if (status == Status.CLOSED || amount <= 0) return false;

        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        // Check credit eligibility first
        if (!isCreditEligible()) return false;
        
        // PROPOSED FIX: add unverified check
        if (status == Status.CLOSED || status == Status.SUSPENDED || status == Status.UNVERIFIED) return false;

        // ORIGINAL LINE
        // if (status == Status.CLOSED || status == Status.SUSPENDED) return false;

        if (amount > balance) return false;
        balance -= amount;
        return true;
    }

    public double getBalance(){
        return balance;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    // add these methods to get status and transition between states

    public Status getStatus() {
        return status;
    }

     public boolean verify() {
        if (status == Status.UNVERIFIED) {
            status = Status.VERIFIED;
            return true;
        }
        return false;
    }

    public boolean violation() {
        if (status == Status.VERIFIED) {
            status = Status.SUSPENDED;
            return true;
        }
        return false;
    }

    public boolean appeal() {
        if (status == Status.SUSPENDED) {
            status = Status.VERIFIED;
            return true;
        }
        return false;
    }

    public boolean adminAction() {
        if (status == Status.SUSPENDED) {
            status = Status.CLOSED;
            return true;
        }
        return false;
    }

    // Credit Score Methods
    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public boolean isCreditEligible() {
        return creditScore >= CREDIT_SCORE_THRESHOLD;
    }

    public static int getCreditScoreThreshold() {
        return CREDIT_SCORE_THRESHOLD;
    }
}