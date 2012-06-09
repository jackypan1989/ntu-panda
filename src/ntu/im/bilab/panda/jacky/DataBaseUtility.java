package ntu.im.bilab.panda.jacky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

public class DataBaseUtility {
	public Connection conn;
    public Statement stmt;
	
	public DataBaseUtility(){
		
		try { 
            Class.forName(Config.DRIVER); 
            conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
            stmt = conn.createStatement();   
        } 
        catch(ClassNotFoundException e) { 
            System.out.println("Can't find driver class"); 
            e.printStackTrace(); 
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void Close()
	{
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
