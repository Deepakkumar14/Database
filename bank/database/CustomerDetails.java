package bank.database;

public class CustomerDetails {
	   private int customerId;
	   private String fullName,city;

	    public int getCustomerId() {
	        return this.customerId;
	    }

	    public void setCustomerId(int customerId) {
	        this.customerId = customerId;
	    }

	    public String getName() {
	        return this.fullName;
	    }

	    public void setName(String full_name) {
	        this.fullName = full_name;
	    }
	    
	    public String getCity() {
	        return this.city;
	    }

	    public void setCity(String city) {
	        this.city = city;
	    }
	    
	    @Override
	    public String toString() {
	        return  "Customer_id: " + customerId +"\t"+" Name: " + fullName +"\t"+" City: " + city+"\n";
	    }
	    
	   
	}


