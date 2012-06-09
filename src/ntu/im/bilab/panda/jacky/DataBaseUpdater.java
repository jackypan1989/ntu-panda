package ntu.im.bilab.panda.jacky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

public class DataBaseUpdater {
	public Connection conn;
    public Statement stmt;
	
	public DataBaseUpdater(){
		try { 
	        Class.forName(Config.DRIVER); 
	        conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
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
	
	public void updateParameter(){
	    for(int i=0 ; i<20 ; i++){
	    	try {
	    		ResultSet result = stmt.executeQuery("SELECT Patent_id FROM patents_index LIMIT 0 , 10000");
	    		while(result.next()){
	    			result.getString("Patent_id");
	    			
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	} 
	    }
	}
}
