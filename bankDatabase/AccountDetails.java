package bankDatabase;
import java.math.BigDecimal;
public class AccountDetails {
	    private int customerId;
	    private long accountNumber;
	    private BigDecimal balance;
	    private String branch;
	    
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

	    public String getBranch(){
	    	return branch;
		}
		public void setBranch(String branch){this.branch=branch;}
	    
	    @Override
	    public String toString() {
	      return  "Account_number: " + accountNumber +"\t"+"Branch: " + branch +"\t"+"Balance: " + balance+"\n";
	    }
	   
	}


