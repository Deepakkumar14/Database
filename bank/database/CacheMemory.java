package bank.database;

import java.util.HashMap;

public enum CacheMemory {
    INSTANCE;
    private  HashMap<Integer,CustomerDetails> customerMap =new HashMap<>();
    private  HashMap<Integer,HashMap<Long,AccountDetails>> accountMap =new HashMap<>();


    public HashMap<Integer,HashMap<Long,AccountDetails>> accountBoolean() {
        return accountMap;
    }

    public HashMap<Long,AccountDetails> accountDetails(int id) {
        return accountMap.get(id);
    }

    public CustomerDetails customerDetails(int id) {
        return customerMap.get(id);
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        if(customerDetails!=null) {
            int cusId = customerDetails.getCustomerId();
            customerMap.put(cusId, customerDetails);
        }
    }

    public boolean deleteCustomer(int id){
        customerMap.remove(id);
        accountMap.remove(id);
        return true;
    }
    public boolean deleteAccount(int id,long accNum){
        HashMap<Long,AccountDetails> account=accountMap.get(id);
        account.remove(accNum);
        return true;
    }

    public void setAccountMap(AccountDetails accountDetails) {
        if(accountDetails!=null) {
            int cusId = accountDetails.getCustomerId();
            HashMap<Long, AccountDetails> accountDetailsHashMap = accountMap.getOrDefault(cusId, new HashMap<>());
            accountDetailsHashMap.put(accountDetails.getAccountNumber(), accountDetails);
            accountMap.put(cusId, accountDetailsHashMap);
        }
    }
 }
