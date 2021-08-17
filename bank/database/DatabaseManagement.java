package bank.database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseManagement {
	private static final String DRIVER =  "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/Zohobank";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "1234";
	private  static Connection conn = null;
	private static PreparedStatement prepStmt =null;
	private static PreparedStatement prepStmt1 =null;
	private  static Statement stmt=null;
	private static ResultSet resultSet =null;
	private static String query="";

	public  void initializeConnection(){
		try{
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	public  Connection getConnection(){
		if(conn == null){
			initializeConnection();
		}
		return conn;
	}


	public  ArrayList<CustomerDetails> dataRetrievalOfCustomer() {
		Connection conn=getConnection();
		ArrayList<CustomerDetails> customerList = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
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
		}finally {
			if (stmt!= null)
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return customerList;
	}


	public  ArrayList<AccountDetails> dataRetrievalOfAccount(){
		Connection conn=getConnection();
		ArrayList<AccountDetails> accountList=new ArrayList<>();
		try  {
			Statement stmt = conn.createStatement();
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
		}finally {
			if (stmt!= null)
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return accountList;
	}

	public ArrayList insertCustomerInfoToTable(ArrayList<ArrayList> details)  {
		Connection conn=getConnection();
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

	public ArrayList insertAccountInfoToTable(AccountDetails accInfo) {
		long accNum=0;
		ResultSet res;
		getConnection();
		ArrayList finalList = new ArrayList();
		try{
			query= "insert into account_details(customer_id,balance,branch) values (?,?,?)";
			prepStmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
				prepStmt.setInt(1, accInfo.getCustomerId());
				prepStmt.setBigDecimal(2, accInfo.getBalance());
			    prepStmt.setString(3, accInfo.getBranch());
			    prepStmt.addBatch();
			   int[] array= prepStmt.executeBatch();
			for (Integer i:array) {
				finalList.add(i);
			}
				res = prepStmt.getGeneratedKeys();
				res.next();
				accNum = res.getInt(1);
				finalList.add(accNum);
		}
		
		catch(BatchUpdateException e){
			try {
				int array[]=e.getUpdateCounts();
				for (Integer i:array) {
					finalList.add(i);
				}
				res= prepStmt.getGeneratedKeys();
				while (res.next()) {
					finalList.add(res.getInt(1));
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
	public int deleteCustomer(int id){
		int condition=0;
		try{
			query = "delete from customer_details where customer_id = ? ";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1,id);
			condition= prepStmt.executeUpdate();
		}catch(Exception e) {
			System.out.println(e);
		}
		return condition;
	}
	//To set the customer id to deactive mode
	public int updateCustomer(int id){
		Connection conn=getConnection();
		int condition=0;
		try{
			conn.setAutoCommit(false);
			query = "update customer_details set status ='Deactive' where customer_id = ? ";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1,id);
			condition= prepStmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			try{
				conn.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			System.out.println(e);
		}
		return condition;
	}
	//To set the account number to deactive mode
	public int deleteAccount(long accNumber){
		Connection conn=getConnection();
		int condition=0;
		try{
			conn.setAutoCommit(false);
			query = "update account_details set status ='Deactive' where account_number= ? ";
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
		}
		return condition;
	}

	public boolean withdrawalAndDeposit(TransactionDetails transDetails, String type){
		Connection conn=getConnection();
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
		}
		return true;
	}


	public Boolean updateBalance(TransactionDetails transDetails, BigDecimal total) {
		Connection conn=getConnection();
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
		}
		return true;
	}

	public static boolean closeConnection() throws Exception{
		conn.close();
		boolean bool=conn.isClosed();
		return bool;
	}

}



