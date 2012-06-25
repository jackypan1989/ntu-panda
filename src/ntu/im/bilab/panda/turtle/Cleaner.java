package ntu.im.bilab.panda.turtle;


import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Cleaner {
	
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://140.112.107.207/patent_value";
	static String user = "root";
	static String password = "123456";
	static int lowestYear = 1976;
	static int highestYear = 2009;
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		long count = 0;	
		try { 
			Class.forName(driver); 
			conn = DriverManager.getConnection(url, user , password);	
			stmt = conn.createStatement(); 
			
			for(int i = lowestYear; i<=highestYear; i++){
				String table_name = "uspto_"+i;
				//ResultSet result = stmt.executeQuery("SELECT Patent_id FROM patents_index LIMIT 0 , 10000"); 
				ResultSet result = stmt.executeQuery("SELECT Patent_id FROM "+table_name); 
				List<String> patent_id_list = new LinkedList<String>(); 
				
				while(result.next()) {
					//String tmp_patent_id = result.getString("Patent_id").trim();
					//if(tmp_patent_id.contains(","))
					patent_id_list.add(result.getString("Patent_id").trim());
					//System.out.println(result.getString("Patent_id"));
				}
				
				count += patent_id_list.size();
				
				Iterator<String> pItr = patent_id_list.iterator();
				while(pItr.hasNext()){
					String patent_id = pItr.next();
					if(!patent_id.contains(","))
						continue;
					
					String new_patent_id = patent_id.replaceAll("," , "");
					stmt.executeUpdate("UPDATE "+table_name+" SET Patent_id = '"+new_patent_id+"' WHERE Patent_id = '"+patent_id+"'");
					System.out.println(patent_id+"->"+new_patent_id);
				}
			}
			System.out.println(count+" patents!");
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