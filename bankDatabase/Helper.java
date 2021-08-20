package bankDatabase;

import java.io.FileReader;

import java.io.IOException;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Helper{

    private Persistence persistence;
    public void objectCreation() {
        try {
            FileReader file=new FileReader("Properties.properties");
                Properties properties = new Properties();
                properties.load(file);
                String value = properties.getProperty("PersistenceObject");
                persistence = (Persistence) Class.forName(value).newInstance();
        } catch (IOException|ClassNotFoundException|InstantiationException|IllegalAccessException  e) {
            System.out.println("Database object not created!!!Check the properties file location");
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void callingDatabaseForAccount() throws CustomException {
        ArrayList<AccountDetails> accountList= persistence.dataRetrievalOfAccount();
        for(int i=0;i<accountList.size();i++){
            CacheMemory.INSTANCE.setAccountMap(accountList.get(i));
        }
    }
    //---------------------------------------------------------------------------------------------
    public void callingDatabaseForCustomer() throws  CustomException{
            ArrayList<CustomerDetails> customerList = persistence.dataRetrievalOfCustomer();
            for (int i = 0; i < customerList.size(); i++) {
                CacheMemory.INSTANCE.setCustomerDetails(customerList.get(i));
            }
    }
    //---------------------------------------------------------------------------------------------
    public void setAllCustomerMap() throws CustomException {
        HashMap<Integer, HashMap<Long, String>> outerMap= persistence.dataRetrievalAllCustomer();
        CacheMemory.INSTANCE.setAllAccountsMap(outerMap);
    }
    //---------------------------------------------------------------------------------------------
    public String retrieveBooleanValue(int id){
        if(CacheMemory.INSTANCE.accountBoolean().containsKey(id)) {
            return "true";
        }
        else{
            return "Invalid customer id!!!!!Enter correct customer id";
        }
    }
    //---------------------------------------------------------------------------------------------
    public String retrieveAccountBooleanValue(int id, long accountNum){
            HashMap<Integer, HashMap<Long, AccountDetails>> accountMap = CacheMemory.INSTANCE.accountBoolean();
                HashMap<Long, AccountDetails> accountDetails = accountMap.get(id);
                if(accountDetails==null){
                    return"Deactivated account number";
                }
                if (accountDetails.containsKey(accountNum)) {
                    return "true";
                }
                else
                  return "Invalid account number !!!!! Enter correct account number.";
    }
    //---------------------------------------------------------------------------------------------
    public String retrieveCustomerDetails(int id) {
        CustomerDetails customerValues=CacheMemory.INSTANCE.customerDetails(id);
        if (customerValues!=null) {
           return customerValues.toString();
        } else
           return "Enter correct customer id";
    }
    //---------------------------------------------------------------------------------------------
    public HashMap<Long, AccountDetails> retrieveAllAccountBalance(int id) {
        HashMap<Long, AccountDetails> accountMap=CacheMemory.INSTANCE.accountDetails(id);
        return accountMap;

    }
    //---------------------------------------------------------------------------------------------
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
    //---------------------------------------------------------------------------------------------
    public HashMap<String, String> checkPoint(ArrayList<ArrayList> details) throws CustomException {
        ArrayList<Integer> successRate= persistence.insertCustomerInfoToTable(details);
        int size=details.size();
       ArrayList<Integer> removeIndex=new ArrayList<>();
        ArrayList<ArrayList> details1=details;
        HashMap<String,String> successAndFailure=new HashMap<>();
        if(successRate.size()==details.size()*2) {
            successAndFailure=insertNewCustomerDetails(details,successRate,size,successAndFailure);
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
            successAndFailure=insertNewCustomerDetails(details1,successRate,size,successAndFailure);
       }
        return successAndFailure;
    }
    //---------------------------------------------------------------------------------------------
    public HashMap insertNewCustomerDetails(ArrayList<ArrayList> details, ArrayList<Integer> successRate, int size, HashMap successAndFailure) throws CustomException {
        for(int i=0;i< details.size();i++) {
            CustomerDetails cusInfo = (CustomerDetails) details.get(i).get(0);
            AccountDetails accInfo = (AccountDetails) details.get(i).get(1);
            int cusId=successRate.get(i+size);
            cusInfo.setCustomerId(cusId);
            accInfo.setCustomerId(cusId);
            ArrayList<Object> accountNum = persistence.insertAccountInfoToTable(accInfo);
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
                persistence.deleteCustomer(accInfo.getCustomerId());
            }
        }
        return successAndFailure;
    }
    //---------------------------------------------------------------------------------------------
    public String insertNewAccountDetails(AccountDetails accDetails) throws CustomException {
        ArrayList<Object> successRate= persistence.insertAccountInfoToTable(accDetails);
        if((Integer)successRate.get(0)>0) {
            accDetails.setAccountNumber((Long)successRate.get(1));
            CacheMemory.INSTANCE.setAccountMap(accDetails);
            return "Account is added"+"\t"+ accDetails;
        }
        else{
            return "Account is not added" +"\t"+ accDetails;
        }
    }
    //---------------------------------------------------------------------------------------------
    public String updateAllAccounts(int id) throws CustomException {
        int condition= persistence.deactivateAllAccounts(id);
        persistence.deactivateCustomer(id);
        if(condition>0) {
            CacheMemory.INSTANCE.deleteCustomer(id);
            return "########## Customer "+id+" Deleted successfully##########";
        }
       else{
           return "Server busy!!!!Try again later later";
        }
    }
    //---------------------------------------------------------------------------------------------
    public String updateAccount(int id, long accNum) throws CustomException {
        int condition = persistence.deactivateAccount(accNum);
        if(condition>0) {
                CacheMemory.INSTANCE.deleteAccount(id,accNum);
                HashMap<Long,AccountDetails> accountDetails=CacheMemory.INSTANCE.accountDetails(id);
                if(accountDetails.size()==0){
                 int value= persistence.deactivateCustomer(id);
                 if(value>0) {
                     CacheMemory.INSTANCE.deleteCustomer(id);
                     return "########## Customer " + id + " Deleted successfully##########\n########## Account " + accNum + " Deleted successfully##########";
                 }else
                     return "Server busy!!!!Try again later later";
                }
                return "########## Account "+accNum+" Deleted successfully##########";
            }
        else
            return "Server busy!!!!Try again later later";
    }
    //---------------------------------------------------------------------------------------------
    public String withdrawal(TransactionDetails transDetails) throws CustomException {
        BigDecimal balance=getBalance(transDetails);
        BigDecimal withdrawalAmount=transDetails.getTransactionAmount();
        int comparedValue=balance.compareTo(withdrawalAmount);
        if(comparedValue>=0){
            BigDecimal total=balance.subtract(withdrawalAmount);
            String type="Withdrawal";
           boolean bool=  persistence.withdrawalAndDeposit(transDetails,type);
           if(bool){
              boolean bool1= persistence.updateBalance(transDetails,total);
              if(bool1) {
                  CacheMemory.INSTANCE.updateBalance(transDetails, total);
                  return "************** Withdrawal of " + withdrawalAmount + " is successful **************";
              }else
                  return "Server Error !!Try again later";
           }
            else
                return "Server Error !!Try again later";
        }
        else
            return "Insufficient balance";
    }
    //---------------------------------------------------------------------------------------------
    public String deposit(TransactionDetails transDetails) throws CustomException {
        BigDecimal balance=getBalance(transDetails);
        BigDecimal depositAmount=transDetails.getTransactionAmount();
        BigDecimal total=balance.add(depositAmount);
        String type="Deposit";
            boolean bool=  persistence.withdrawalAndDeposit(transDetails,type);
            if(bool){
                boolean bool1= persistence.updateBalance(transDetails,total);
                if(bool1) {
                    CacheMemory.INSTANCE.updateBalance(transDetails, total);
                    return "************ Deposit of " + depositAmount + " is successful ***************";
                } else
                    return "Server Error !!Try again later";
            }
            else{
                return "Server Error !!Try again later";
            }
        }
    //---------------------------------------------------------------------------------------------
    public BigDecimal getBalance(TransactionDetails transDetails){
        HashMap<Integer,HashMap<Long,AccountDetails>> accountMap=CacheMemory.INSTANCE.accountBoolean();
        HashMap<Long,AccountDetails>accountDetails=accountMap.get(transDetails.getCustomerId());
        AccountDetails accInfo=accountDetails.get(transDetails.getAccountNumber());
        BigDecimal balance=accInfo.getBalance();
        return  balance;
    }
    //---------------------------------------------------------------------------------------------
    public String allCustomers(int customerId){
        HashMap<Integer, HashMap<Long, String>> outerMap= CacheMemory.INSTANCE.getAllAccountsMap();
        if(outerMap.containsKey(customerId)) {
            return "true";
        }
        return "Invalid customer id!!!!!Enter correct customer id";
    }
    //---------------------------------------------------------------------------------------------
    public String allAccounts(int id,long accNum){
        HashMap<Integer, HashMap<Long, String>> outerMap= CacheMemory.INSTANCE.getAllAccountsMap();
        HashMap<Long,String> innerMap=  outerMap.get(id);
        if(innerMap.containsKey(accNum)) {
            return "true";
        }
        return "Invalid account number !!!!! Enter correct account number.";
    }
    //---------------------------------------------------------------------------------------------
    public String activateAccounts(int id,long accNum) throws CustomException {
        int condition= persistence.activateAccount(id,accNum);
        if(condition>0){
            callingDatabaseForCustomer();
            callingDatabaseForAccount();
            return "*******Account is activated*********";
        }
        return "Account is not activated!!Server busy!!!Try again later";
    }
    //---------------------------------------------------------------------------------------------
//    public void transferAmount(){
//
//    }
    //---------------------------------------------------------------------------------------------
       public String closeConnection() throws CustomException {
           if(persistence.closeConnection())
               return "Connection is closed ";
           else
               return  "Connection is Not closed";
       }
}

