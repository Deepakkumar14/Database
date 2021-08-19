package bankDatabase;

import java.util.*;

public class BalanceChecking {
	private static final Scanner input=new Scanner(System.in);
	private static final Helper helper=new Helper();

	public static void main(String[] args){
		BalanceChecking balanceChecking= new BalanceChecking();
		helper.objectCreation();
		helper.callingDatabaseForCustomer();
		helper.callingDatabaseForAccount();
		balanceChecking.userChoice();
	}

	public  void userChoice(){
		while(true) {
			System.out.println("1.Existing user-Need to check Balance");
			System.out.println("2.Existing user-Need to add new account");
			System.out.println("3.New Customer-Need to Enter Customer details");
			System.out.println("4.To Delete an  Customer or account");
			System.out.println("5.Amount Transaction(Withdrawal or Deposit)");
			System.out.println("6.To activate the account");
			System.out.println("7. Exit");
			System.out.println();
			System.out.println("Enter your choice");
			int choice = input.nextInt();

			AccountDetails accountDetails = new AccountDetails();

			if (choice == 1) {
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				int value=0;
				if (helper.retrieveBooleanValue(customerId)) {
				System.out.println("Enter 1 to check all account balance  \nEnter 2 to check particular account balance\n");
				value = input.nextInt();
				if (value == 1) {
						String details = helper.retrieveCustomerDetails(customerId);
						HashMap<Long, AccountDetails> accountMap = helper.retrieveAllAccountBalance(customerId);
						System.out.print(details);
						for (AccountDetails values : accountMap.values()) {
							System.out.print(values);
						}
					} else {
					System.out.println("Enter 1,2 or 3");

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
					System.out.println("Invalid Customer id !!!!! Enter correct customer id.");
				}
				System.out.println();
			}
			//-----------------------------------------------------------------------------------------
			else if (choice == 2) {
				System.out.println("Enter the customer_id to store in accounts table");
				int customerId=input.nextInt();
				if (helper.retrieveBooleanValue(customerId)) {
					accountDetails.setCustomerId(customerId);
					System.out.println("Enter account balance");
					accountDetails.setBalance(input.nextBigDecimal());
					input.nextLine();
					System.out.println("Enter the branch name");
					accountDetails.setBranch(input.nextLine());
					String output = helper.insertNewAccountDetails(accountDetails);
					System.out.println(output);
				}
				else{
					System.out.println("Invalid Customer id !!!!! Enter correct customer id.");
				}
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

					System.out.println("Enter the details for account "+i);

					System.out.println("Enter the account balance");
					accountDetails.setBalance(input.nextBigDecimal());
					input.nextLine();
					System.out.println("Enter the branch name");
					accountDetails.setBranch(input.nextLine());

					innerArrayList.add(customerDetails);
					innerArrayList.add(accountDetails);
					details.add(innerArrayList);

				}
				HashMap<String, String> successAndFailure=helper.checkPoint(details);
				for (Map.Entry entry:successAndFailure.entrySet()) {
					System.out.println(entry.getValue()+" == "+entry.getKey());
				}
				System.out.println();
			}
			//----------------------------------------------------------------------------------------------
			else if(choice ==4) {
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				if (helper.retrieveBooleanValue(customerId)) {
					System.out.println("Enter 1 To delete particular Account  \nEnter 2 To delete customer\n");
					int value = input.nextInt();
					switch (value) {
						case 1: {
							System.out.println("Enter account number");
							long accNum = input.nextLong();
							if (helper.retrieveAccountBooleanValue(customerId, accNum)) {
								if (helper.updateAccount(customerId, accNum)) {
									System.out.println("########## Account Deleted ##########");
								} else {
									System.out.println("Server busy!!!!Try again later");
								}
							} else {
								System.out.println("Invalid account number");
							}
							break;
						}
						case 2: {
							if (helper.updateAllAccounts(customerId)) {
								System.out.println("######### customer Deleted #########");
							} else {
								System.out.println("Server busy!!!!Try again later");
							}
							break;
						}
						default:
							System.out.println("Invalid choice!!!!Enter 1 or 2");
					}
				}
				else {
					System.out.println("Invalid customer id");
				}
			}

			//---------------------------------------------------------------------------------------------
			else if(choice ==5){
				TransactionDetails transDetails=new TransactionDetails();
				System.out.println("Enter customerId");
				transDetails.setCustomerId(input.nextInt());
				int id= transDetails.getCustomerId();
				if(helper.retrieveBooleanValue(id)) {
					System.out.println("Enter the account number from which you have to withdraw or deposit");
					transDetails.setAccountNumber(input.nextLong());
					long accNum=transDetails.getAccountNumber();
					if (helper.retrieveAccountBooleanValue(id,accNum)) {
						System.out.println("Enter \n1.Deposit\n2.Withdrawal");
						int option = input.nextInt();
						switch (option) {
							case 1: {
								System.out.println("Enter the amount to Deposit: ");
								transDetails.setTransactionAmount(input.nextBigDecimal());
								input.nextLine();
								System.out.println("Enter the type of transaction account \nSavings account\nCurrent account: ");
								transDetails.setTransactionType(input.nextLine());
								boolean bool = helper.deposit(transDetails);
								if (bool) {
									System.out.println("************ Deposit of " + transDetails.getTransactionAmount() + " is successful ***************");
								} else {
									System.out.println("Server Error !!Try again");
								}
								break;
							}
							case 2: {
								System.out.println("Enter the amount to withdraw: ");
								transDetails.setTransactionAmount(input.nextBigDecimal());
								input.nextLine();
								System.out.println("Enter the type of transaction account \nSavings account\nCurrent account: ");
								transDetails.setTransactionType(input.nextLine());
								boolean bool = helper.withdrawal(transDetails);
								if (bool) {
									System.out.println("************** Withdrawal of " + transDetails.getTransactionAmount() + " is successful **************");
								} else {
									System.out.println("Insufficient balance");
								}
								break;
							}
							default:{
								System.out.println("Invalid option!!!!\nEnter 1 or 2");
							}
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
				System.out.println("Enter customerId");
				int customerId = input.nextInt();
				if(helper.activateAccounts().containsKey(customerId)){
					System.out.println("Enter account number");
					long accNum = input.nextLong();

				}

			}
			//----------------------------------------------------------------------------------------------
			else if (choice == 7) {
				boolean bool=helper.closeConnection();
				if(bool) {
					System.out.println("Connection is closed ");
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
