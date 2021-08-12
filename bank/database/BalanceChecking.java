package bank.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
			System.out.println("4. Exit");
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
				helper.insertNewAccountDetails(accountDetails);
				System.out.println();

			}
		//	-------------------------------------------------------------------------------------------
			else if (choice == 3) {
				System.out.println("Enter the number of new customers");
				int customers = input.nextInt();
				input.nextLine();
				ArrayList<ArrayList> details = new ArrayList<>();
				while (customers-- > 0) {
					CustomerDetails customerDetails = new CustomerDetails();
					accountDetails = new AccountDetails();

					ArrayList innerArrayList = new ArrayList(2);
					System.out.println("Enter the user name");
					customerDetails.setName(input.nextLine());

					System.out.println("Enter the user city");
					customerDetails.setCity(input.nextLine());

					System.out.println("Enter the account balance");
					accountDetails.setBalance(input.nextBigDecimal());
					input.nextLine();

					innerArrayList.add(customerDetails);
					innerArrayList.add(accountDetails);
					details.add(innerArrayList);

				}
				helper.insertNewCustomerDetails(details);
				System.out.println();
			}
			//----------------------------------------------------------------------------------------------
			else if (choice == 4) {
				DatabaseManagement.closeConnection();
				break;
			}
			//----------------------------------------------------------------------------------------------
			else {
				System.out.println("Enter valid choice");
			}
		}
	}
}
