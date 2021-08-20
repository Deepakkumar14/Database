package bankDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public interface Persistence  {

    ArrayList<CustomerDetails> dataRetrievalOfCustomer() throws CustomException;

    ArrayList<AccountDetails> dataRetrievalOfAccount() throws CustomException;

    ArrayList insertCustomerInfoToTable(ArrayList<ArrayList> details) throws CustomException;

    ArrayList insertAccountInfoToTable(AccountDetails accInfo) throws CustomException;

    //To delete customer id that is entered during customer insertion but failed during account insertion
    void deleteCustomer(int id) throws CustomException;

    //To set the customer id to deactive mode
    int deactivateAllAccounts(int id) throws CustomException;

    int deactivateCustomer(int id) throws CustomException;

    //To retrieve all details both active and inactive
    HashMap<Integer, HashMap<Long, String>> dataRetrievalAllCustomer() throws CustomException;

    //To set the account number to deactive mode
    int deactivateAccount(long accNumber) throws CustomException;

    //To activate account and customer
    int activateAccount(int id,long accNum) throws CustomException;

    boolean withdrawalAndDeposit(TransactionDetails transDetails, String type) throws CustomException;

//After withdrawal or deposit the account balance in the accounts table is updated
    boolean updateBalance(TransactionDetails transDetails, BigDecimal total) throws CustomException;

    boolean closeConnection() throws CustomException;
}
