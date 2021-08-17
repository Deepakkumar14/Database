package bank.database;

import java.math.BigDecimal;
import java.util.*;

public class BalanceChecking {
	private static final Scanner input=new Scanner(System.in);
	private static final Helper helper=new Helper();
	public static void main(String[] args) throws Exception{
		BalanceChecking balanceChecking= new BalanceChecking();
		helper.callingDatabaseForCustomer();
		helper.callingDatabaseForAccount();
		balanceChecking.userChoice();
	}

	public  void userChoice() throws Exception{
		while(true) {
			System.out.println("1.Existing user-Need to check Balance");
			System.out.println("2.Existing user-Need to add new account");
			System.out.println("3.New Customer-Need to Enter Customer details");
			System.out.println("4.To Delete an  Customer or account");
			System.out.println("5.To withdraw amount from particular account");
			System.out.println("6.To withdraw amount from particular account");
			System.out.println("7. Exit");
			System.out.println();
			System.out.println("Enter your choice");
			int choice = input.nextInt();

			AccountDetails accountDetails = new AccountDetails();

			if (choice == 1) {
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				System.out.println("Enter 1 to check all account balance  \nEnter 2 to check particular account balance\n");
				int value = input.nextInt();
				if (value == 1) {
					if (helper.retrieveBooleanValue(customerId)) {
						String details = helper.retrieveCustomerDetails(customerId);
						HashMap<Long, AccountDetails> accountMap = helper.retrieveAllAccountBalance(customerId);
						System.out.print(details);
						for (AccountDetails values : accountMap.values()) {
							System.out.print(values);
						}
					} else {
						System.out.println("Invalid Customer id !!!!! Enter correct customer id.");
					}
					System.out.println();

				} else if (value == 2) {
					System.out.println("Enter the account number");
					long accNumber = input.nextInt();
					if (helper.retrieveBooleanValue(customerId)) {
						String details = helper.retrieveCustomerDetails(customerId);
						String accountValue = helper.retrieveParticularAccountBalance(accNumber, customerId);
						System.out.print(details);
						System.out.print(accountValue);
					} else {
						System.out.println("Invalid Customer id !!!!! Enter correct customer id.");
					}
					System.out.println();
				} else {
					System.out.println("Enter 1,2 or 3");
				}
				System.out.println();
			}
			//-----------------------------------------------------------------------------------------
			else if (choice == 2) {
				System.out.println("Enter the customer_id to store in accounts table");
				accountDetails.setCustomerId(input.nextInt());
				System.out.println("Enter account balance");
				accountDetails.setBalance(input.nextBigDecimal());
				input.nextLine();
				System.out.println("Enter the branch name");
				accountDetails.setBranch(input.nextLine());
				String output=helper.insertNewAccountDetails(accountDetails);
				System.out.println(output);
				System.out.println();

			}
		//	-------------------------------------------------------------------------------------------
			else if (choice == 3) {
				System.out.println("Enter the number of new customers");
				int customers = input.nextInt();
				input.nextLine();
				ArrayList<ArrayList> details = new ArrayList<>();
				for(int i=1;i<=customers;i++) {
					CustomerDetails customerDetails = new CustomerDetails();
					accountDetails = new AccountDetails();

					System.out.println("Enter the details for customer "+i);
					ArrayList innerArrayList = new ArrayList(2);
					System.out.println("Enter the user name");
					customerDetails.setName(input.nextLine());

					System.out.println("Enter the user city");
					customerDetails.setCity(input.nextLine());

					System.out.println("Enter the account balance");
					accountDetails.setBalance(input.nextBigDecimal());
					input.nextLine();
					System.out.println("Enter the branch name");
					accountDetails.setBranch(input.nextLine());

					innerArrayList.add(customerDetails);
					innerArrayList.add(accountDetails);
					details.add(innerArrayList);

				}
				HashMap<Object, String> successAndFailure=helper.checkPoint(details);
				for (Map.Entry entry:successAndFailure.entrySet()) {
					System.out.println(entry.getValue()+"="+entry.getKey());
				}
				System.out.println();
			}
			//----------------------------------------------------------------------------------------------
			else if(choice ==4){
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				System.out.println("Enter 1 To delete customer  \nEnter 2 To delete particular account\n");
				int value = input.nextInt();
				if(value==1){
					if(helper.deleteCustomer(customerId)){
						System.out.println("Account Deleted");
					}
				}
			}
			//---------------------------------------------------------------------------------------------
			else if(choice ==5){
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				if(helper.retrieveBooleanValue(customerId)) {
					System.out.println("Enter the account number from which you have to withdraw");
					long accountNum = input.nextLong();
					if (helper.retrieveAccountBooleanValue(customerId, accountNum)) {
						System.out.print("Enter the amount to withdraw: ");
						BigDecimal amount = input.nextBigDecimal();
						Boolean bool = helper.withdrawal(customerId, accountNum, amount);
						if (bool) {
							System.out.println("Withdrawal of " + amount + " is successful");
						} else {
							System.out.println("Insufficient balance");
						}
					}
					else {
						System.out.println("Invalid account number");
					}
				}
				else{
					System.out.println("Invalid customer id");
					}
			    }
			//----------------------------------------------------------------------------------------------
			else if (choice == 6) {
				boolean bool=helper.closeConnection();
				if(bool) {
					System.out.println("Connection is closed: "+bool);
				}
				else {
					System.out.println("Connection is Not closed");
				}
				break;
			}
			//----------------------------------------------------------------------------------------------
			else {
				System.out.println("Enter valid choice");
			}
		}
	}
}
