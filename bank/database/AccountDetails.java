package bank.database;
import java.math.BigDecimal;
public class AccountDetails {
	    private int customerId;
	    private long accountNumber;
	    private BigDecimal balance;
	    
	    public int getCustomerId() {
	        return customerId;
	    }

	    public void setCustomerId(int customerId) {
	        this.customerId = customerId;
	    }

	    public long getAccountNumber() {
	        return accountNumber;
	    }
	    
	    public void setAccountNumber(long accountNumber) {
	        this.accountNumber = accountNumber;
	    }

	    public BigDecimal getBalance() {
	        return balance;
	    }

	    public void setBalance(BigDecimal balance) {
	        this.balance = balance;
	    }
	    
	    @Override
	    public String toString() {
	      return  "Account_number: " + accountNumber +"\t"+"Balance: " + balance+"\n";
	    }
	   
	}


