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
		//4080180 , 136007
		for(int i=0 ; i<1; i++){
			try {
	    		ResultSet result = stmt.executeQuery("SELECT Patent_id FROM value LIMIT "+i*30+" , 30");
	    		while(result.next()){
	    			String patent_id = result.getString("Patent_id");
	    			Patent patent = new Patent(patent_id);
	    			System.out.println(patent_id);
	    			
	    			ParameterFinder t = new ParameterFinder();
	    			ResultSet new_data = patent.getNewData();
	    			ResultSet old_data = patent.getOldData();
	   
	    			t.getEC(patent_id);
	    			patent.setParameterForeignInventors(t.getForeignInventors(old_data.getString("Inventors")));
	    			patent.setParameterForeignClasses(t.getForeignClasses(old_data.getString("References Cited")));
	    			patent.setParameterPatentFamilySize(t.getPatentFamilySize(patent_id));
	    			patent.setParameterPatentedBackwardCitations(t.getPatentedBackwardCitations(patent_id));
	    			patent.setParameterMajorMarket(t.getMajorMarket(patent_id));
	    			patent.setParameterForeignPriorityApps(t.getForeignPriorityApps(old_data.getString("Current U.S. Class")));
	    			patent.setParameterYearsToReceiveTheFirstCitation(t.getYearsToReceiveTheFirstCitation(patent));
	    			System.out.println(patent.toString());
	    			
	    			
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	} 
		}
		
		
		
		/*
	    for(int i=0 ; i<20 ; i++){
	    	try {
	    		ResultSet result = stmt.executeQuery("SELECT Patent_id FROM value LIMIT 0 , 10000");
	    		while(result.next()){
	    			result.getString("Patent_id");
	    			
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	} 
	    }*/
	}
	
	public static void main(String[] args)
	{
		DataBaseUpdater dbu = new DataBaseUpdater();
		dbu.updateParameter();
	}
}
