package bankDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface Persistence {

    ArrayList<CustomerDetails> dataRetrievalOfCustomer();

    ArrayList<AccountDetails> dataRetrievalOfAccount();

    ArrayList insertCustomerInfoToTable(ArrayList<ArrayList> details);

    ArrayList insertAccountInfoToTable(AccountDetails accInfo);

    //To delete customer id that is entered during customer insertion but failed during account insertion
    int deleteCustomer(int id);

    //To set the customer id to deactive mode
    int updateAllAccounts(int id);

    int updateCustomer(int id);

    //To set the account number to deactive mode
    int deleteAccount(long accNumber);

    boolean withdrawalAndDeposit(TransactionDetails transDetails, String type);

//After withdrawal or deposit the account balance in the accounts table is updated
    boolean updateBalance(TransactionDetails transDetails, BigDecimal total);

    boolean closeConnection() ;
}
