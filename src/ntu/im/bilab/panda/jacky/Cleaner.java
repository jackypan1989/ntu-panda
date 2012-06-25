package ntu.im.bilab.panda.jacky;

import java.sql.*;

import ntu.im.bilab.panda.core.Config;

public class Cleaner {
	public static void main(String[] args) {
		Connection conn = null;
        Statement stmt = null;
		try { 
            Class.forName(Config.DRIVER); 
            conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER ,  Config.DATABASE_PASSWORD);
            
            stmt = conn.createStatement(); 
            
            ResultSet result = stmt.executeQuery("SELECT Patent_id FROM patents_index LIMIT 0 , 10000"); 
            String[] patent_id = new String[10000];
            
            int i = 0;
            while(result.next()) { 
            	patent_id[i] = result.getString("Patent_id");
            	i++;
            }
            
            for(int j = 0 ; j<patent_id.length ; j++){
            	String new_patent_id = patent_id[j].replaceAll(",", "");
            	stmt.executeUpdate("UPDATE patents_index SET Patent_id = '"+new_patent_id+"' WHERE Patent_id = '"+patent_id[j]+"'");
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
