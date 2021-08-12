package bank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManagement {
	private static final String DRIVER =  "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/bank1";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "1234";
	private  static Connection conn = null;
	private static PreparedStatement prepStmt =null;
	private static PreparedStatement prepStmt1 =null;
	private  static Statement stmt=null;
	private static ResultSet resultSet =null;


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
			resultSet = stmt.executeQuery("select * from  customer_details");
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
		getConnection();
		ArrayList<AccountDetails> accountList=new ArrayList<>();
		try  {
			Statement stmt = conn.createStatement();
			resultSet= stmt.executeQuery("select * from account_details");
			while(resultSet.next()){
				AccountDetails accountInfoToMap=new AccountDetails();
				int cusId=resultSet.getInt("customer_id");
				long accNo=resultSet.getLong("account_number");
				accountInfoToMap.setCustomerId(cusId);
				accountInfoToMap.setAccountNumber(accNo);
				accountInfoToMap.setBalance(resultSet.getBigDecimal("balance"));
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

	public  ArrayList<Integer> insertCustomerInfoToTable(ArrayList<ArrayList> details) {
		getConnection();
		ArrayList<Integer> customerIdList=new ArrayList<>();
		try{
			String query = "insert into customer_details (full_name,city) values (?,?)";
			prepStmt1 = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for(int i=0;i< details.size();i++) {
				CustomerDetails cusInfo = (CustomerDetails) details.get(i).get(0);
				prepStmt1.setString(1, cusInfo.getName());
				prepStmt1.setString(2, cusInfo.getCity());
				prepStmt1.executeUpdate();
				ResultSet res= prepStmt1.getGeneratedKeys();
				res.next();
				int cusId=res.getInt(1);
				System.out.println("CustomerId :"+cusId+"\t"+"Name :"+cusInfo.getName());
				customerIdList.add(cusId);
			}
			//prepStmt1.executeBatch();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (prepStmt1 !=null)
				try {
					prepStmt1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		System.out.println("Values inserted successfully");
		System.out.println("----------------------------------");
		return customerIdList;
	}

	public  long insertAccountInfoToTable(AccountDetails accInfo) {
		long accNum=0;
		getConnection();
		try{
			String query1 = "insert into account_details(customer_id,balance) values (?,?)";
			prepStmt = conn.prepareStatement(query1,Statement.RETURN_GENERATED_KEYS);
				prepStmt.setInt(1, accInfo.getCustomerId());
				prepStmt.setBigDecimal(2, accInfo.getBalance());
				prepStmt.executeUpdate();
				ResultSet res = prepStmt.getGeneratedKeys();
				res.next();
				accNum = res.getInt(1);
				System.out.println("AccountNumber :" + accNum + "\t" + "Balance :" + accInfo.getBalance());
			//prepStmt.executeBatch();
		}
		catch(Exception e){
			e.printStackTrace();
		}finally {
			if (prepStmt !=null)
				try {
					prepStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		System.out.println("Values inserted successfully");
		System.out.println("----------------------------------");
		return accNum;
	}


	public static void closeConnection() throws Exception{
		conn.close();
		boolean bool=conn.isClosed();
		if(bool) {
			System.out.println("Database is closed: "+bool);
		}
		else {
			conn.close();
			System.out.println("Database is closed");
		}
	}

}


