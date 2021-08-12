package bank.database;

import java.util.ArrayList;
import java.util.HashMap;

public class Helper{
    DatabaseManagement databaseManagement=new DatabaseManagement();

    public void callingDatabaseForAccount() {
        ArrayList<AccountDetails> accountList= databaseManagement.dataRetrievalOfAccount();
        for(int i=0;i<accountList.size();i++){
            CacheMemory.INSTANCE.setAccountMap(accountList.get(i));
        }
    }

    public void callingDatabaseForCustomer(){
        ArrayList<CustomerDetails> customerList = databaseManagement.dataRetrievalOfCustomer();
        for(int i=0;i<customerList.size();i++){
            CacheMemory.INSTANCE.setCustomerDetails(customerList.get(i));
        }
    }

    public boolean retrieveBooleanValue(int id){
        return CacheMemory.INSTANCE.accountBoolean().containsKey(id);
    }

    public String retrieveCustomerDetails(int id) {

        CustomerDetails customerValues=CacheMemory.INSTANCE.customerDetails(id);

        if (customerValues!=null) {
           return customerValues.toString();

        } else {
           return "Enter correct customer id";
        }
    }


    public HashMap<Long, AccountDetails> retrieveAllAccountBalance(int id) {
        HashMap<Long, AccountDetails> accountMap=CacheMemory.INSTANCE.accountDetails(id);
        return accountMap;

    }


    public String retrieveParticularAccountBalance(long accNum, int id)  {
        HashMap<Long, AccountDetails> accountMap=CacheMemory.INSTANCE.accountDetails(id);
            if (accountMap.get(accNum)!=null) {
                AccountDetails accountValue=accountMap.get(accNum);
               return accountValue.toString();
            }
            else {
                return "Invalid account number";
            }
        }


    public void insertNewCustomerDetails(ArrayList<ArrayList> details) {
        ArrayList<Integer> customerId=databaseManagement.insertCustomerInfoToTable(details);
        for(int i=0;i< details.size();i++) {
            CustomerDetails cusInfo = (CustomerDetails) details.get(i).get(0);
            AccountDetails accInfo = (AccountDetails) details.get(i).get(1);
            int cusId=customerId.get(i);
            cusInfo.setCustomerId(cusId);
            accInfo.setCustomerId(cusId);
           insertNewAccountDetails(accInfo);
            CacheMemory.INSTANCE.setCustomerDetails(cusInfo);
        }
    }

    public void insertNewAccountDetails(AccountDetails accDetails) {
        long accountNumber=databaseManagement.insertAccountInfoToTable(accDetails);
        accDetails.setAccountNumber(accountNumber);
        CacheMemory.INSTANCE.setAccountMap(accDetails);
       }
    }





