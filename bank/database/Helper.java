package bank.database;

import java.math.BigDecimal;
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

    public boolean retrieveAccountBooleanValue(int id,long accountNum){
        HashMap<Integer,HashMap<Long,AccountDetails>> accountMap=CacheMemory.INSTANCE.accountBoolean();
       if(accountMap.containsKey(id)){
           HashMap<Long,AccountDetails>accountDetails=accountMap.get(id);
           if(accountDetails.containsKey(accountNum)){
               return true;
           }
       }
           return false;
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
    public HashMap<String, String> checkPoint(ArrayList<ArrayList> details){
        ArrayList<Integer> successRate= databaseManagement.insertCustomerInfoToTable(details);
        int size=details.size();
       ArrayList<Integer> removeIndex=new ArrayList<>();
        ArrayList<ArrayList> details1=details;
        HashMap<String,String> successAndFailure=new HashMap<>();
        if(successRate.size()==details.size()*2) {
            insertNewCustomerDetails(details,successRate,size,successAndFailure);
        }
        else {
            for(int i=0;i< details.size();i++) {
                if (successRate.get(i) < 0) {
                    CustomerDetails cusInfo=(CustomerDetails) details1.get(i).get(0);
                    String value=cusInfo.toString();
                    successAndFailure.put(value,"Failed to add customer details");
                    removeIndex.add(i);
                }
            }
            for (int i= removeIndex.size()-1;i>=0;i--) {
                int value=removeIndex.get(i);
                details1.remove(value);
            }
            insertNewCustomerDetails(details1,successRate,size,successAndFailure);
       }
        return successAndFailure;
    }

    public HashMap insertNewCustomerDetails(ArrayList<ArrayList> details, ArrayList<Integer> successRate, int size, HashMap successAndFailure) {
        for(int i=0;i< details.size();i++) {
            CustomerDetails cusInfo = (CustomerDetails) details.get(i).get(0);
            AccountDetails accInfo = (AccountDetails) details.get(i).get(1);
            int cusId=successRate.get(i+size);
            cusInfo.setCustomerId(cusId);
            accInfo.setCustomerId(cusId);
            ArrayList<Object> accountNum =databaseManagement.insertAccountInfoToTable(accInfo);
            if((Integer)accountNum.get(0)>0) {
                accInfo.setAccountNumber((Long)accountNum.get(1));
                String value=cusInfo + accInfo.toString();
                successAndFailure.put(value,"Successfully added both details");
                CacheMemory.INSTANCE.setCustomerDetails(cusInfo);
                CacheMemory.INSTANCE.setAccountMap(accInfo);
            }
            else{
                String value=accInfo.toString();
                successAndFailure.put(value,"Failed to add account details ");
                databaseManagement.deleteCustomer(accInfo.getCustomerId());
            }
        }
        return successAndFailure;
    }

    public String insertNewAccountDetails(AccountDetails accDetails) {
        ArrayList<Object> successRate=databaseManagement.insertAccountInfoToTable(accDetails);
        if((Integer)successRate.get(0)>0) {
            accDetails.setAccountNumber((Long)successRate.get(1));
            CacheMemory.INSTANCE.setAccountMap(accDetails);
            return "Account is added"+"\t"+ accDetails;
        }
        else{
            return "Account is not added" +"\t"+ accDetails;
        }
    }

    public boolean deleteCustomer(int id){
        int condition=databaseManagement.updateCustomer(id);
        if(condition>0) {
            Boolean bool = CacheMemory.INSTANCE.deleteCustomer(id);
            return bool;
        }
       else{
           return false;
        }
    }

    public boolean deleteAccount(int id,long accNum){
        int condition = databaseManagement.deleteAccount(accNum);
        if(condition>=0) {
            Boolean bool=CacheMemory.INSTANCE.deleteAccount(id,accNum);
            return bool;
        }
        else{
            return false;
        }
    }

    public boolean withdrawal(TransactionDetails transDetails){
        BigDecimal balance=getBalance(transDetails);
        BigDecimal withdrawalAmount=transDetails.getTransactionAmount();
        int comparedValue=balance.compareTo(withdrawalAmount);
        if(comparedValue>=0){
            BigDecimal total=balance.subtract(withdrawalAmount);
            String type="Withdrawal";
           Boolean bool=  databaseManagement.withdrawalAndDeposit(transDetails,type);
           if(bool){
               Boolean bool1=databaseManagement.updateBalance(transDetails,total);
               CacheMemory.INSTANCE.updateBalance(transDetails,total);
               return  bool1;
           }
            else{
                return false;
           }
        }
        else{
            return false;
        }
    }
    public Boolean deposit(TransactionDetails transDetails) {
        BigDecimal balance=getBalance(transDetails);
        BigDecimal depositAmount=transDetails.getTransactionAmount();
        BigDecimal total=balance.add(depositAmount);
        String type="Deposit";
            Boolean bool=  databaseManagement.withdrawalAndDeposit(transDetails,type);
            if(bool){
                Boolean bool1=databaseManagement.updateBalance(transDetails,total);
                CacheMemory.INSTANCE.updateBalance(transDetails,total);
                return  bool1;
            }
            else{
                return false;
            }
        }

    public BigDecimal getBalance(TransactionDetails transDetails){
        HashMap<Integer,HashMap<Long,AccountDetails>> accountMap=CacheMemory.INSTANCE.accountBoolean();
        HashMap<Long,AccountDetails>accountDetails=accountMap.get(transDetails.getCustomerId());
        AccountDetails accInfo=accountDetails.get(transDetails.getAccountNumber());
        BigDecimal balance=accInfo.getBalance();
        return  balance;
    }

       public boolean closeConnection() throws Exception {
           return DatabaseManagement.closeConnection();
       }


}





