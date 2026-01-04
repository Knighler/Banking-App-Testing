// TransactionProcessor.java
public class TransactionProcessor {
    
    public boolean processTransfer(Account source, Account target, double amount) {
        if (source == null || target == null) return false;

        boolean withdrawSuccess = source.withdraw(amount);
        
        if (withdrawSuccess) {
            boolean depositSuccess = target.deposit(amount);
            
            if (!depositSuccess) {

                source.deposit(amount); 
                return false; 
            }
            return true; 
        }
        
        return false; 
    }
}