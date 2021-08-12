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
        int cusId=customerDetails.getCustomerId();
        customerMap.put(cusId,customerDetails);
    }

    public void setAccountMap(AccountDetails accountDetails) {
        int cusId=accountDetails.getCustomerId();
        HashMap<Long,AccountDetails> accountDetailsHashMap = accountMap.getOrDefault(cusId,new HashMap<>());
        accountDetailsHashMap.put(accountDetails.getAccountNumber(),accountDetails);
        accountMap.put(cusId,accountDetailsHashMap);
    }
 }
