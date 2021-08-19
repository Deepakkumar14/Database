package bankDatabase;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseManagement implements Persistence {
	private static Connection conn = null;
	private static PreparedStatement prepStmt =null;
	private static PreparedStatement prepStmt1 =null;
	private static ResultSet resultSet =null;
	private static String query="";

	public  DatabaseManagement() throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZohoBank", "root", "1234");

		} catch (ClassNotFoundException|SQLException e) {
			throw new CustomException("Not connected to the Database!!!Connection error");
		}
	}

	@Override
	public  ArrayList<CustomerDetails> dataRetrievalOfCustomer() {
		ArrayList<CustomerDetails> customerList = new ArrayList<>();
		try (Statement stmt = conn.createStatement()){
			resultSet = stmt.executeQuery("select * from  customer_details where status='Active'");
			while (resultSet.next()) {
				CustomerDetails customerInfoToMap = new CustomerDetails();
				int cusId = resultSet.getInt("customer_id");
				customerInfoToMap.setCustomerId(cusId);
				customerInfoToMap.setName(resultSet.getString("full_name"));
				customerInfoToMap.setCity(resultSet.getString("city"));
				customerList.add(customerInfoToMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return customerList;
	}


	@Override
	public  ArrayList<AccountDetails> dataRetrievalOfAccount(){
		ArrayList<AccountDetails> accountList=new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			resultSet= stmt.executeQuery("select * from account_details where status='Active'");
			while(resultSet.next()){
				AccountDetails accountInfoToMap=new AccountDetails();
				int cusId=resultSet.getInt("customer_id");
				long accNo=resultSet.getLong("account_number");
				accountInfoToMap.setCustomerId(cusId);
				accountInfoToMap.setAccountNumber(accNo);
				accountInfoToMap.setBalance(resultSet.getBigDecimal("balance"));
				accountInfoToMap.setBranch(resultSet.getString("branch"));
				accountList.add(accountInfoToMap);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return accountList;
	}

	@Override
	public ArrayList insertCustomerInfoToTable(ArrayList<ArrayList> details)  {
		ResultSet res;
		int[] successRate;
		ArrayList finalList = new ArrayList();
		try{
			query = "insert into customer_details (full_name,city) values (?,?)";
			 prepStmt1 = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for(int i=0;i< details.size();i++) {
				CustomerDetails cusInfo = (CustomerDetails) details.get(i).get(0);
				prepStmt1.setString(1, cusInfo.getName());
				prepStmt1.setString(2, cusInfo.getCity());
				prepStmt1.addBatch();
			}
			successRate=prepStmt1.executeBatch();
			for (Integer i:successRate) {
				finalList.add(i);
			}
			 res= prepStmt1.getGeneratedKeys();
			while (res.next()) {
				finalList.add(res.getInt(1));
			}
		}
		catch(BatchUpdateException e){
			try {
				successRate=e.getUpdateCounts();
				for (Integer i:successRate) {
					finalList.add(i);
				}
				res= prepStmt1.getGeneratedKeys();
				while (res.next()) {
					finalList.add(res.getInt(1));
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (prepStmt1 !=null)
				try {
					prepStmt1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return finalList;
	}

	@Override
	public ArrayList insertAccountInfoToTable(AccountDetails accInfo) {
		long accNum=0;
		ResultSet res;
		ArrayList finalList = new ArrayList();
		try{
			conn.setAutoCommit(false);
			query= "insert into account_details(customer_id,balance,branch) values (?,?,?)";
			prepStmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
				prepStmt.setInt(1, accInfo.getCustomerId());
				prepStmt.setBigDecimal(2, accInfo.getBalance());
			    prepStmt.setString(3, accInfo.getBranch());
			    prepStmt.addBatch();
			   int[] array= prepStmt.executeBatch();
			   conn.commit();
			for (Integer i:array) {
				finalList.add(i);
			}
				res = prepStmt.getGeneratedKeys();
				res.next();
				accNum = res.getInt(1);
				finalList.add(accNum);
		}
		catch(BatchUpdateException e){
			System.out.println(e);
			try {
				int[] array=e.getUpdateCounts();
				for (Integer i:array) {
					finalList.add(i);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return finalList;
	}
	//To delete customer id that is entered during customer insertion but failed during account insertion
	@Override
	public int deleteCustomer(int id){
		int condition=0;
		try{
			query = "delete from customer_details where customer_id = ? ";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1,id);
			condition= prepStmt.executeUpdate();
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return condition;
	}

	//To set the all accounts to deactive mode
	@Override
	public int updateAllAccounts(int id){
		int condition=0;
		try{
			conn.setAutoCommit(false);
			query="update account_details set status ='Deactive' where customer_id = ?";
				prepStmt = conn.prepareStatement(query);
				prepStmt.setInt(1, id);
				condition = prepStmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			try{
				conn.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			System.out.println(e);
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return condition;
	}
	//To deactivate customer in customer table
	@Override
	public int updateCustomer(int id){
		int condition=0;
		try{
			conn.setAutoCommit(false);
			String query1="update customer_details set status ='Deactive' where customer_id= ?";
				prepStmt1 = conn.prepareStatement(query1);
				prepStmt1.setInt(1, id);
				condition = prepStmt1.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			System.out.println(e);
			try{
				conn.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		}finally {
			if (prepStmt1 !=null)
				try {
					prepStmt1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return condition;
	}
	//To set the account number to deactive mode
	@Override
	public int deleteAccount(long accNumber){
		int condition=0;
		try{
			conn.setAutoCommit(false);
			query = "update account_details set status ='Deactive' where account_number= ?";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setLong(1,accNumber);
			condition= prepStmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			System.out.println(e);
			try{
				conn.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return condition;
	}

	@Override
	public boolean withdrawalAndDeposit(TransactionDetails transDetails, String type){
		try{
			conn.setAutoCommit(false);
			query = "INSERT INTO transaction_details(customer_id,account_number,transaction_type,transaction_amount,date,status)values(?,?,?,?,null,?)";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1,transDetails.getCustomerId());
			prepStmt.setLong(2,transDetails.getAccountNumber());
			prepStmt.setString(3,transDetails.getTransactionType());
			prepStmt.setBigDecimal(4,transDetails.getTransactionAmount());
			prepStmt.setString(5,type);
			prepStmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			System.out.println(e);
			try {
				conn.rollback();
				return false;
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return true;
	}

	//After withdrawal or deposit the account balance in the accounts table is updated
	@Override
	public boolean updateBalance(TransactionDetails transDetails, BigDecimal total) {
		try{
			conn.setAutoCommit(false);
			query = "update account_details set balance=? where customer_id =? AND account_number=?";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(2,transDetails.getCustomerId());
			prepStmt.setLong(3,transDetails.getAccountNumber());
			prepStmt.setBigDecimal(1,total);
			prepStmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			System.out.println(e);
			try {
				conn.rollback();
				return false;
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		return true;
	}

@Override
	public boolean closeConnection() {
		try {
			conn.close();
			boolean bool = conn.isClosed();
			return bool;
		} catch (SQLException exception) {
			System.out.println(exception);
			return false;
		}
	}

}



