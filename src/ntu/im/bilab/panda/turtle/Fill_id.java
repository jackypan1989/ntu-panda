package ntu.im.bilab.panda.turtle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Fill_id {
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://140.112.107.207/patent_value";
	static String user = "root";
	static String password = "123456";
	static int lowestYear = 1976;
	static int highestYear = 2009;
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		String insertTable = "patent_id_year";
		
		try { 
			Class.forName(driver); 
			conn = DriverManager.getConnection(url, user , password);	
			stmt = conn.createStatement(); 
			
			for(int i = lowestYear; i<=highestYear; i++){
				String table_name = "uspto_"+i;
				ResultSet result = stmt.executeQuery("SELECT Patent_id FROM "+table_name); 
				List<String> patent_id_list = new LinkedList<String>(); 
				
				while(result.next()) {
					patent_id_list.add(result.getString("Patent_id").trim());
				}
				
				Iterator<String> pItr = patent_id_list.iterator();
				while(pItr.hasNext()){
					String patent_id = pItr.next();				
					stmt.execute("INSERT INTO`patent_value`.`"+insertTable+"` (`Patent_id`, `Patent_year`) VALUES ('"+patent_id+"', '"+i+"')");
					System.out.println(patent_id+" "+i+" inserted");
				}
			}	
		} 
		catch(ClassNotFoundException e) { 
			System.out.println("Can't find driver class"); 
			e.printStackTrace(); 
		} 
		catch(SQLException e) { 
			e.printStackTrace(); 
		}
		finally {
			if(stmt != null) {
				try {
					stmt.close();
				} 
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
}
