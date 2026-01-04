public class ClientController {

    private TransactionProcessor processor = new TransactionProcessor();


    public String handleDeposit(Account account, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            boolean success = account.deposit(amount); 
            return success ? "Deposit successful" : "Deposit failed";
        } catch (NumberFormatException e) {
            return "Error: Invalid input format";
        }
    }

 
    public String handleWithdraw(Account account, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
   
            boolean success = account.withdraw(amount);
            return success ? "Withdrawal successful" : "Withdrawal failed";
        } catch (NumberFormatException e) {
            return "Error: Invalid input format";
        }
    }


    public String handleTransfer(Account source, Account target, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);

            boolean success = processor.processTransfer(source, target, amount);
            return success ? "Transfer successful" : "Transfer failed";
        } catch (NumberFormatException e) {
            return "Error: Invalid input format";
        }
    }

    public String handleViewStatement(Account account) {
 
        double balance = account.getBalance();
        return "Balance: $" + String.format("%.2f", balance);
    }
}